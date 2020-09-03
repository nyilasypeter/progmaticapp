/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.QuestionDTO;
import com.progmatic.progmappbe.dtos.QuestionSearchDto;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import com.progmatic.progmappbe.exceptions.UnauthorizedException;
import com.progmatic.progmappbe.helpers.ResultBuilder;
import com.progmatic.progmappbe.helpers.SecHelper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private void guessAnswerTypeIfNeeded(PossibleAnswer possibleAnswer) {
        if (possibleAnswer.getType() == null) {
            Set<PossibleAnswerValue> possibleAnswerValues = possibleAnswer.getPossibleAnswerValues();
            if (possibleAnswerValues.size() == 1) {
                possibleAnswer.setType(PossibleAnswerType.trueFalseCheckbox);
            } else if (possibleAnswerValues.stream().filter(pav -> pav.getIsRightAnswer()).count() > 1) {
                possibleAnswer.setType(PossibleAnswerType.checkboxList);
            } else {
                possibleAnswer.setType(PossibleAnswerType.radioButtons);
            }
        }
    }

    private void checkPossibleAnswer(PossibleAnswer possibleAnswer, EntityCreationResult result) {
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
            default:
                break;
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
            whereCondition.and(question.text.contains(searchDto.getQuestionText()));
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
        questions.stream().forEach(q -> ret.add(mapper.map(q, QuestionDTO.class)));
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
        QuestionDTO qdto = mapper.map(q, QuestionDTO.class);
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
    public BasicResult uplaoFileToQuestion(
            String questionId,
            MultipartFile file){
        if(em.find(Question.class, questionId) == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", questionId, resultBuilder.translate("progmapp.entity.question"));
        }
        return attachmentService.uploadOneFileToOneEntity(questionId, file);
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "') or hasAuthority('" + Privilige.PRIV_START_TEST + "')")
    public ResponseEntity<Resource> loadImageOfQuestion(String questionId){
        return  attachmentService.loadOneToOneFile(questionId);

    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    public BasicResult uplaoFileToPossibleAnswer(
            String possibleAnswerId,
            MultipartFile file){
        if(em.find(PossibleAnswer.class, possibleAnswerId) == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", possibleAnswerId, resultBuilder.translate("progmapp.entity.possibleanswer"));
        }
        return attachmentService.uploadOneFileToOneEntity(possibleAnswerId, file);
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "') or hasAuthority('" + Privilige.PRIV_START_TEST + "')")
    public ResponseEntity<Resource> loadImageOfPossibleAnswer(String possibleAnswerId){
        return  attachmentService.loadOneToOneFile(possibleAnswerId);

    }


}
