/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.quiz.*;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import com.progmatic.progmappbe.exceptions.UnauthorizedException;
import com.progmatic.progmappbe.helpers.ResultBuilder;
import com.progmatic.progmappbe.helpers.SecHelper;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.progmatic.progmappbe.helpers.sourceevaluator.EvaluationResult;
import com.progmatic.progmappbe.helpers.sourceevaluator.SoruceCodeEvaluator;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author peti
 */
@Service
public class TestService {

    private DozerBeanMapper mapper;

    @Autowired
    private TestService self;

    @PersistenceContext
    private EntityManager em;

    private AttachmentService attachmentService;

    private ResultBuilder resultBuilder;

    @Autowired
    public TestService(DozerBeanMapper mapper, AttachmentService attachmentService, ResultBuilder resultBuilder) {
        this.attachmentService = attachmentService;
        this.mapper = mapper;
        this.resultBuilder = resultBuilder;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public EntityCreationResult createQuestion(QuestionDTO q) {
        Question question = mapper.map(q, Question.class);
        EntityCreationResult ret = new EntityCreationResult();
        for (PossibleAnswer possibleAnswer : question.getPossibleAnswers()) {
            possibleAnswer.setQuestion(question);
            guessAnswerTypeIfNeeded(possibleAnswer);
            checkPossibleAnswer(possibleAnswer, ret);
            for (PossibleAnswerValue possibleAnswerValue : possibleAnswer.getPossibleAnswerValues()) {
                possibleAnswerValue.setPossibleAnswer(possibleAnswer);
            }
        }
        if(!ret.getErrorMessages().isEmpty()){
            ret.setSuccessFullResult(false);
        }
        else {
            em.persist(question);
            ret.setSuccessFullResult(true);
            ret.setIdOfCreatedEntity(question.getId());
        }
        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public BasicResult updateQuestion(QuestionDTO q) {
        Question updatedQuestion = mapper.map(q, Question.class);
        BasicResult ret = new BasicResult();
        //checkUpdateQuestion(updatedQuestion);
        if(!ret.getErrorMessages().isEmpty()){
            ret.setSuccessFullResult(false);
            return ret;
        }
        for (PossibleAnswer possibleAnswer : updatedQuestion.getPossibleAnswers()) {
            guessAnswerTypeIfNeeded(possibleAnswer);
            checkPossibleAnswer(possibleAnswer, ret);
        }
        if(!ret.getErrorMessages().isEmpty()){
            ret.setSuccessFullResult(false);
        }
        else {
            Question origQuestion = em.find(Question.class, q.getId());
            origQuestion.setAdminDescription(q.getAdminDescription());
            origQuestion.setAnswerTimeInSec(q.getAnswerTimeInSec());
            origQuestion.setEvaluationAlogrithm(q.getEvaluationAlogrithm());
            origQuestion.setExplanationAfter(q.getExplanationAfter());
            origQuestion.setFeedbackType(q.getFeedbackType());
            origQuestion.setText(q.getText());
            updatePossibleAnswers(q, origQuestion);
            ret.setSuccessFullResult(true);
        }
        return ret;
    }
    
    private void updatePossibleAnswers(QuestionDTO  updatedQuestion, Question origQuestion){
        List<PossibleAnswer> possibleAnswersToDelete = new ArrayList<>();
        for (PossibleAnswer origPossibleAnswer : origQuestion.getPossibleAnswers()) {
            Optional<PossibleAnswerDTO> newPo = updatedQuestion.getPossibleAnswers().stream().filter(pa -> origPossibleAnswer.getId().equals(pa.getId())).findFirst();
            if(newPo.isPresent()){
                PossibleAnswerDTO updatedPossibelAnswer = newPo.get();
                origPossibleAnswer.setType(updatedPossibelAnswer.getType());
                origPossibleAnswer.setTextBefore(updatedPossibelAnswer.getTextBefore());
                origPossibleAnswer.setTextAfter(updatedPossibelAnswer.getTextBefore());
                updatePossibleAnswerValue(updatedPossibelAnswer, origPossibleAnswer);
            }
            else{   //delete those possible answers which were not in the update request
                possibleAnswersToDelete.add(origPossibleAnswer);
            }
        }
        for (PossibleAnswer possibleAnswer : possibleAnswersToDelete) {
            possibleAnswer.setQuestion(null);
            em.remove(possibleAnswer);
        }
        //create those possible answers which were in the request but not in the db
        for (PossibleAnswerDTO updatedPossibleAnswer : updatedQuestion.getPossibleAnswers()) {
            if(!origQuestion.getPossibleAnswers().stream().filter(po -> po.getId().equals(updatedPossibleAnswer.getId())).findFirst().isPresent()){
                PossibleAnswer pa = mapper.map(updatedPossibleAnswer, PossibleAnswer.class);
                pa.setQuestion(origQuestion);
                for (PossibleAnswerValue possibleAnswerValue : pa.getPossibleAnswerValues()) {
                    possibleAnswerValue.setPossibleAnswer(pa);
                }
                em.persist(pa);
            }
        }
    }
    
    private void updatePossibleAnswerValue(PossibleAnswerDTO updatedPossibleAnswer, PossibleAnswer origPossibleAnswer){
        List<PossibleAnswerValue> possibleAnswerValuesToDelete = new ArrayList<>();
        for (PossibleAnswerValue origPossibleAnswerValue : origPossibleAnswer.getPossibleAnswerValues()) {
            Optional<PossibleAnswerValueDTO> first = updatedPossibleAnswer.getPossibleAnswerValues().stream().filter(pv -> origPossibleAnswerValue.getId().equals(pv.getId())).findFirst();
            if(first.isPresent()){
                PossibleAnswerValueDTO updatedPossibleAnswerValue = first.get();
                origPossibleAnswerValue.setText(updatedPossibleAnswerValue.getText());
                origPossibleAnswerValue.setIsRightAnswer(updatedPossibleAnswerValue.getIsRightAnswer());
            }
            else{ //delete those possible answers which were not in the update request
                possibleAnswerValuesToDelete.add(origPossibleAnswerValue);

            }
        }
        for (PossibleAnswerValue possibleAnswerValue : possibleAnswerValuesToDelete) {
            possibleAnswerValue.setPossibleAnswer(null);
            em.remove(possibleAnswerValue);
        }
        //create those possible answer values which were not in the db
        for (PossibleAnswerValueDTO possibleAnswerValue : updatedPossibleAnswer.getPossibleAnswerValues()) {
            if(!origPossibleAnswer.getPossibleAnswerValues().stream().filter(pv -> pv.getId().equals(possibleAnswerValue.getId())).findFirst().isPresent()){
                PossibleAnswerValue pv = new PossibleAnswerValue();
                pv.setPossibleAnswer(origPossibleAnswer);
                pv.setText(possibleAnswerValue.getText());
                pv.setIsRightAnswer(possibleAnswerValue.getIsRightAnswer());
                em.persist(pv);
            }
        }
    }

    private void guessAnswerTypeIfNeeded(PossibleAnswer possibleAnswer) {
        if (possibleAnswer.getType() == null) {
            Set<PossibleAnswerValue> possibleAnswerValues = possibleAnswer.getPossibleAnswerValues();
            if (possibleAnswerValues.size() == 1) {
                possibleAnswer.setType(PossibleAnswerType.trueFalseCheckbox);
            } else if (possibleAnswerValues.stream().filter(pav -> pav.getIsRightAnswer() != null && pav.getIsRightAnswer()).count() > 1) {
                possibleAnswer.setType(PossibleAnswerType.checkboxList);
            }
            else if(possibleAnswerValues.stream().filter(pav -> pav.getRightOrder() != null).count() > 0){
                possibleAnswer.setType(PossibleAnswerType.soruceCodeToOrder_EvalByCompare);
            }
            else if(StringUtils.isNotBlank(possibleAnswer.getUnitTestCode())){
                possibleAnswer.setType(PossibleAnswerType.soruceCodeToOrder_EvalByRun);
            }
            else {
                possibleAnswer.setType(PossibleAnswerType.radioButtons);
            }
        }
    }

    private void checkPossibleAnswer(PossibleAnswer possibleAnswer, BasicResult result) {
        switch (possibleAnswer.getType()) {
            case trueFalseCheckbox:
                if (possibleAnswer.getPossibleAnswerValues().size() != 1) {
                    result.addErrorMessage("progmapp.error.questionvalidation.truelfasechekbox", resultBuilder.translate("progmapp.error.questionvalidation.truelfasechekbox"));
                }
                break;
            case radioButtons:
            case dropdown:
                if (possibleAnswer.getPossibleAnswerValues().stream().filter(pav -> pav.getIsRightAnswer()).count()  != 1) {
                    result.addErrorMessage("progmapp.error.questionvalidation.radio", resultBuilder.translate("progmapp.error.questionvalidation.radio"));
                }
                break;
            case longText:
            case shortText:
                if (!possibleAnswer.getPossibleAnswerValues().isEmpty()) {
                    result.addErrorMessage("progmapp.error.questionvalidation.freetext", resultBuilder.translate("progmapp.error.questionvalidation.freetext"));
                }
                break;
            case soruceCodeToOrder_EvalByCompare:
                if(possibleAnswer.getPossibleAnswerValues().stream().filter(pav -> pav.getRightOrder() == null).count()  != 0){
                    result.addErrorMessage("progmapp.error.questionvalidation.soruceCodeToOrder_noOrder", resultBuilder.translate("progmapp.error.questionvalidation.soruceCodeToOrder_noOrder"));
                }
                break;
            case soruceCodeToOrder_EvalByRun:
                if(possibleAnswer.getPossibleAnswerValues().stream().filter(pav -> pav.getRightOrder() == null).count()  != 0){
                    result.addErrorMessage("progmapp.error.questionvalidation.soruceCodeToOrder_noOrder", resultBuilder.translate("progmapp.error.questionvalidation.soruceCodeToOrder_noOrder"));
                }
                if(StringUtils.isBlank(possibleAnswer.getUnitTestCode())){
                    result.addErrorMessage("progmapp.error.questionvalidation.soruceCodeToOrder_EvalByRun_noCode", resultBuilder.translate("progmapp.error.questionvalidation.soruceCodeToOrder_EvalByRun_noCode"));
                }
                if(result.getErrorMessages().isEmpty()){
                    runUnitTestOnOrderCode(possibleAnswer, result);
                }
                break;
            default:
                break;
        }
    }

    private void runUnitTestOnOrderCode(PossibleAnswer possibleAnswer, BasicResult result) {
        List<PossibleAnswerValue> orederedAnswerValues = new ArrayList<>(possibleAnswer.getPossibleAnswerValues());
        Collections.sort(orederedAnswerValues, Comparator.comparing(PossibleAnswerValue::getRightOrder));
        String suggestedAnswer = orederedAnswerValues.stream()
                .map(actualAnswerValue -> actualAnswerValue.getText())
                .collect(Collectors.joining("\n"));
        String unitTest = possibleAnswer.getUnitTestCode();
        SoruceCodeEvaluator soruceCodeEvaluator = new SoruceCodeEvaluator();
        EvaluationResult evaluationResult = soruceCodeEvaluator.evaluateSourceCode(suggestedAnswer, unitTest);
        if(!evaluationResult.isSuccessfull()){
            if(!evaluationResult.getCompilationSuccessfull()){
                result.addErrorMessage(
                        "progmapp.error.questionvalidation.soruceCodeToOrder_EvalByRun_noCompile",
                        resultBuilder.translate("progmapp.error.questionvalidation.soruceCodeToOrder_EvalByRun_noCompile"));
            }
            else if(!evaluationResult.getUnitTestSuccessfull()){
                result.addErrorMessage(
                        "progmapp.error.questionvalidation.soruceCodeToOrder_EvalByRun_UnitTest_NotSuccessfull",
                        resultBuilder.translate("progmapp.error.questionvalidation.soruceCodeToOrder_EvalByRun_UnitTest_NotSuccessfull"));
            }
            else {
                result.addErrorMessage("progmapp.error.szarvanapalcsintaban", resultBuilder.translate("progmapp.error.szarvanapalcsintaban"));
            }
        }
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public String modifyQuestion(QuestionDTO q) {
        return null;
    }

    /**
     * Copies a question and returns the id of the copy.
     *
     * @param questionId
     * @return
     */
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public String copyQuestion(String questionId) {
        EntityGraph<?> eg = em.createEntityGraph("questionWithPossibleAnswerAndPossibleAnswerValues");
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("javax.persistence.fetchgraph", eg);
        Question q = em.find(Question.class, questionId, propMap);
        if (q == null) {
            return null;
        }
        Question newQ = SerializationUtils.clone(q);
        newQ.setId(null);
        newQ.setUpdatedAt(null);
        for (PossibleAnswer pa : newQ.getPossibleAnswers()) {
            pa.setId(null);
            pa.setUpdatedAt(null);
            for (PossibleAnswerValue pav : pa.getPossibleAnswerValues()) {
                pav.setId(null);
                pav.setUpdatedAt(null);
            }
        }
        em.persist(newQ);
        return newQ.getId();
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public List<QuestionDTO> findQuestions(QuestionSearchDto searchDto){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        BooleanBuilder whereCondition = new BooleanBuilder();
        QQuestion question = QQuestion.question;
        if(StringUtils.isNotBlank(searchDto.getQuestionText())){
            whereCondition.and(question.text.like("%"+searchDto.getQuestionText()+"%"));
        }
        if(StringUtils.isNotBlank(searchDto.getUploader())){
            whereCondition.and(question.createdBy.eq(searchDto.getUploader()));
        }
        if(searchDto.getUploadTimeFrom() != null){
            whereCondition.and(question.createdAt.after(searchDto.getUploadTimeFrom()));
        }
        if(searchDto.getUploadTimeTo() != null){
            whereCondition.and(question.createdAt.before(searchDto.getUploadTimeTo()));
        }
        if(searchDto.getNotInEternalQuiz() != null && searchDto.getNotInEternalQuiz()){
            whereCondition.and(question.eternalQuizs.isEmpty());
        }
        QPossibleAnswer qPossibleAnswer = QPossibleAnswer.possibleAnswer;
        List<Question> questions = queryFactory.selectFrom(question)
                .leftJoin(question.eternalQuizs).fetchJoin()
                .join(question.possibleAnswers, qPossibleAnswer).fetchJoin()
                .join(qPossibleAnswer.possibleAnswerValues).fetchJoin()
                .where(whereCondition)
                .distinct()
                .fetch();

        List<QuestionDTO> ret = new ArrayList<>();
        questions.stream().forEach(q -> ret.add(mapper.map(q, QuestionDTO.class, "mapAll")));
        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "') or hasAuthority('" + Privilige.PRIV_START_TEST + "')")
    @Transactional
    public QuestionDTO findQuestion(String id) {
        EntityGraph<?> eg = em.createEntityGraph("questionWithPossibleAnswerAndPossibleAnswerValues");
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("javax.persistence.fetchgraph", eg);
        Question q = em.find(Question.class, id, propMap);
        if (q == null) {
            return null;
        }
        self.checkHasPermissionToReadQuestion(q);
        QuestionDTO qdto;
        if(SecHelper.hasAuthority(Privilige.PRIV_CREATE_QUESTION)) {
            qdto = mapper.map(q, QuestionDTO.class, "mapAll");
        }
        else{
            qdto = mapper.map(q, QuestionDTO.class, "omitIsRightAnswerInfo");
        }
        return qdto;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void checkHasPermissionToReadQuestion(Question q) {
        if (!SecHelper.hasAuthority(Privilige.PRIV_CREATE_QUESTION)) {
            throw new UnauthorizedException("TODO check that the logged in user has an active test which contains this question");
            //TODO check that the logged in user has an active test which contains this question
        }
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public BasicResult uplaoFileToQuestion(
            String questionId,
            MultipartFile file){
        Question question = em.find(Question.class, questionId);
        if(question == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", questionId, resultBuilder.translate("progmapp.entity.question"));
        }
        question.setHasImage(true);
        return attachmentService.uploadOneFileToOneEntity(questionId, file);
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "') or hasAuthority('" + Privilige.PRIV_START_TEST + "')")
    public ResponseEntity<Resource> loadImageOfQuestion(String questionId){
        return  attachmentService.loadOneToOneFile(questionId);

    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public BasicResult uplaoFileToPossibleAnswer(
            String possibleAnswerId,
            MultipartFile file){
        PossibleAnswer possibleAnswer = em.find(PossibleAnswer.class, possibleAnswerId);
        if(possibleAnswer == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", possibleAnswerId, resultBuilder.translate("progmapp.entity.possibleanswer"));
        }
        possibleAnswer.setHasImage(true);
        return attachmentService.uploadOneFileToOneEntity(possibleAnswerId, file);
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "') or hasAuthority('" + Privilige.PRIV_START_TEST + "')")
    public ResponseEntity<Resource> loadImageOfPossibleAnswer(String possibleAnswerId){
        return  attachmentService.loadOneToOneFile(possibleAnswerId);

    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    public EntityCreationResult createOrderLinesQuestion(OrderLinesQuestionRequestDTO oqr) {
        QuestionDTO questionDTO = new QuestionDTO();
        mapper.map(oqr, questionDTO);
        PossibleAnswerDTO po = new PossibleAnswerDTO();
        questionDTO.getPossibleAnswers().add(po);
        po.setType(PossibleAnswerType.soruceCodeToOrder_EvalByRun);
        po.setUnitTestCode(oqr.getUnitTest());
        String[] codeLines = oqr.getCode().split("\n");
        int i=1;
        for (String codeLine : codeLines) {
            String trimmedLine = codeLine.trim();
            if(StringUtils.isNotBlank(trimmedLine)) {
                PossibleAnswerValueDTO pov = new PossibleAnswerValueDTO();
                pov.setRightOrder(i++);
                pov.setText(trimmedLine);
                po.getPossibleAnswerValues().add(pov);
            }

        }
        return self.createQuestion(questionDTO);
    }
}
