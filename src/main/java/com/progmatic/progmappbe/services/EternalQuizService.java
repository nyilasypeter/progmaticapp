package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.eternalquiz.*;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionDTO;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.helpers.ResultBuilder;
import com.progmatic.progmappbe.helpers.SecHelper;
import com.progmatic.progmappbe.anwerevaluator.AnswerEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EternalQuizService {

    public static final int PROBABILITY_TO_CHOOSE_NEW_QUESTION = 65;
    public static final int PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION = 25;
    public static final int PROBABILITY_TO_CHOOSE_WELL_ANSWERED_QUESTION = 10;

    private Random random = new Random();


    @Autowired
    private EternalQuizService self;

    @PersistenceContext
    private EntityManager em;

    private DozerBeanMapper mapper;

    private ApplicationContext context;

    private ConstantService constantService;

    private ResultBuilder resultBuilder;

    @Autowired
    public EternalQuizService(DozerBeanMapper mapper, ApplicationContext context, ConstantService constantService, ResultBuilder resultBuilder) {
        this.mapper = mapper;
        this.context = context;
        this.constantService = constantService;
        this.resultBuilder = resultBuilder;
    }


    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public EntityCreationResult createEternalQuiz(EternalQuizDTO edto){
        EternalQuiz eq = new EternalQuiz();
        if(StringUtils.isNotBlank(edto.getId())){
            if(em.find(EternalQuiz.class, edto.getId()) != null){
                return resultBuilder.errorEntityCreateResult("progmapp.error.idalreadyexists", edto.getId(), resultBuilder.translate("progmapp.entity.eternalquiz"));
            }
            eq.setId(edto.getId());
        }

        EntityCreationResult ret = new EntityCreationResult();
        for (String questionId : edto.getQuestionIds()) {
            Question question = em.find(Question.class, questionId);
            if(question != null){
                eq.addQuestion(question);
            }
            else{
                ret.addNote(resultBuilder.translate("progmapp.error.iddoesnotexist", questionId, resultBuilder.translate("progmapp.entity.question")));
            }

        }
        em.persist(eq);
        ret.setSuccessFullResult(true);
        ret.setIdOfCreatedEntity(eq.getId());
        return ret;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public BasicResult assignQuestionToEternalQuiz(String eternqlQizId, String questionId){
        EternalQuiz eternalQuiz = em.find(EternalQuiz.class, eternqlQizId);
        Question question = em.find(Question.class, questionId);
        if(eternalQuiz == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", eternqlQizId, resultBuilder.translate("progmapp.entity.eternalquiz"));
        }
        if(question == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", questionId, resultBuilder.translate("progmapp.entity.question"));
        }
        List resultList = em.createQuery("select e from EternalQuiz e inner join e.questions q where e.id = :eQId and q.id = :qId")
                .setParameter("eQId", eternqlQizId)
                .setParameter("qId", questionId)
                .getResultList();
        if(!resultList.isEmpty()){
            return resultBuilder.errorResult("progmapp.error.equizalreadycontainsquestion");
        }
        eternalQuiz.addQuestion(question);
        self.fillEternalQuizAnswers(eternalQuiz, question);
        return resultBuilder.okResult();
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public void fillEternalQuizAnswers(EternalQuiz eternalQuiz, Question question){
        Set<SchoolClass> schoolClasses = eternalQuiz.getSchoolClasses();
        for (SchoolClass schoolClass : schoolClasses) {
            for (User student : schoolClass.getStudents()) {
                EternalQuizAnswer ea = new EternalQuizAnswer();
                ea.setHasAnswer(false);
                ea.setQuestion(question);
                ea.setStudent(student);
                em.persist(ea);
            }
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public BasicResult assignQuestionToEternalSchoolClass(String eternqlQizId, String schoolClassId){
        EternalQuiz eternalQuiz = em.find(EternalQuiz.class, eternqlQizId);
        SchoolClass schoolClass = em.find(SchoolClass.class, schoolClassId);
        if(eternalQuiz == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", eternqlQizId, resultBuilder.translate("progmapp.entity.eternalquiz"));
        }
        if(schoolClass == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", schoolClassId, resultBuilder.translate("progmapp.entity.schoolClass"));
        }
        List resultList = em.createQuery("select e from EternalQuiz e inner join e.schoolClasses c where e.id = :eQId and c.id = :classId")
                .setParameter("eQId", eternqlQizId)
                .setParameter("classId", schoolClassId)
                .getResultList();
        if(!resultList.isEmpty()){
            return resultBuilder.errorResult("progmapp.error.equizalreadycontainsclass");
        }
        eternalQuiz.addSchoolClass(schoolClass);
        self.fillAllEternalQuizAnswers(eternalQuiz);
        return resultBuilder.okResult();
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public void fillAllEternalQuizAnswers(EternalQuiz eternalQuiz){
        Set<SchoolClass> schoolClasses = eternalQuiz.getSchoolClasses();
        Set<Question> questions = eternalQuiz.getQuestions();
        for (SchoolClass schoolClass : schoolClasses) {
            for (User student : schoolClass.getStudents()) {
                for (Question question : questions) {
                    EternalQuizAnswer ea = new EternalQuizAnswer();
                    ea.setHasAnswer(false);
                    ea.setQuestion(question);
                    ea.setStudent(student);
                    em.persist(ea);
                }
            }
        }
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public List<EternalQuizSearchResponseDTO> searchEternalQuizes(){
        List<EternalQuiz> resultList = em.createQuery("select e from EternalQuiz  e left join e.schoolClasses", EternalQuiz.class)
                .getResultList();
        List<EternalQuizSearchResponseDTO> ret = new ArrayList<>();
        resultList.stream().forEach(eq -> ret.add(mapper.map(eq, EternalQuizSearchResponseDTO.class)));
        return ret;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "')")
    public QuestionDTO getNextEternalQuizQuestion() {
        User loggedInUser = SecHelper.getLoggedInUser();
        List<EternalQuizAnswer> quizes =
                em.createQuery("select  eq from EternalQuizAnswer eq left join fetch eq.lastAnswer where eq.student.id = :studentId",
                        EternalQuizAnswer.class)
                        .setParameter("studentId", loggedInUser.getId())
                        .getResultList();
        EternalQuizAnswer randomAnswer = getRandomAnswer(quizes);
        QuestionDTO qdto = null;
        if (randomAnswer != null) {
            randomAnswer.setTimeOfLastAccess(System.currentTimeMillis());
            randomAnswer.setWasSentAsAQuestion(true);
            EntityGraph entityGraph = em.getEntityGraph("questionWithPossibleAnswerAndPossibleAnswerValues");
            Map<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.loadgraph", entityGraph);
            Question question = em.find(Question.class, randomAnswer.getQuestion().getId(), properties);
            qdto = mapper.map(question, QuestionDTO.class, "omitIsRightAnswerInfo");
        }
        return qdto;
    }

    private EternalQuizAnswer getRandomAnswer(List<EternalQuizAnswer> quizes) {
        if (quizes.isEmpty()) {
            return null;
        }
        int ran = random.nextInt(100);
        EternalQuizAnswer selectedQuiz = null;
        if (ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION) {
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> !q.getHasAnswer())
                    .collect(Collectors.toList());
            if (!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        } else if (ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION + PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION) {
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> q.getHasAnswer())
                    .filter(q -> (q.getLastAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                    .collect(Collectors.toList());
            if (!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        } else if (ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION + PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION + PROBABILITY_TO_CHOOSE_WELL_ANSWERED_QUESTION) {
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> q.getHasAnswer())
                    .filter(q -> (!q.getLastAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                    .collect(Collectors.toList());
            if (!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        }
        if (selectedQuiz == null) {
            selectedQuiz = quizes.get(random.nextInt(quizes.size()));
        }
        return selectedQuiz;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "')")
    public AnswerFeedbackDTO acceptEternalQuizAnswer(AnswerResponseDTO answer){
        User user = SecHelper.getLoggedInUser();
        EternalQuizAnswer eternalQuizAnswer = findEternalQuizAnswer(answer, user);
        if(eternalQuizAnswer == null){
            return new AnswerFeedbackDTO(resultBuilder.errorResult("progmapp.error.noeternqlquizforuserquestion", user.getId(), answer.getQuestionId()));

        }
        AnswerFeedbackDTO ret = new AnswerFeedbackDTO();
        checkQuizResponse(answer, eternalQuizAnswer, ret);
        if(!ret.getErrorMessages().isEmpty()){
            ret.setSuccessFullResult(false);
            return ret;
        }
        eternalQuizAnswer.setWasSentAsAQuestion(false);
        Question question = eternalQuizAnswer.getQuestion();
        if(question.getAnswerTimeInSec() != null) {
            long answerTimeInSec = (System.currentTimeMillis() - eternalQuizAnswer.getTimeOfLastAccess()) / 1000;
            if(answerTimeInSec > question.calculatedAnswerTimeInSec()){
                AnswerFeedbackDTO answerFeedbackDTO = new AnswerFeedbackDTO();
                answerFeedbackDTO.setSuccessFullResult(true);
                answerFeedbackDTO.setResult(AnswerEvaulationResult.lateAnswer);
                return answerFeedbackDTO;
            }
        }
        ActualAnswer actualAnswer = createAnswer(answer, eternalQuizAnswer);
        AnswerEvaluator ev = getAnswerEvaluator(question);
        AnswerEvaulationResult result = ev.evaluateAnswer(actualAnswer);
        actualAnswer.setAnswerEvaulationResult(result);
        em.persist(actualAnswer);
        eternalQuizAnswer.setHasAnswer(true);
        if(result.isWrongAnswer()){
            eternalQuizAnswer.oneBadTrial();
        }
        else{
            eternalQuizAnswer.oneGoodTrial();
        }
        AnswerFeedbackDTO resp = getResponse(question, result);
        return resp;
    }

    private EternalQuizAnswer findEternalQuizAnswer(AnswerResponseDTO answer, User user) {
        try{
            EternalQuizAnswer eternalQuizAnswer = em.createQuery(
                    "select e from EternalQuizAnswer e JOIN FETCH e.question where e.question.id = :questionId and e.student.id = :studentId",
                    EternalQuizAnswer.class)
                    .setParameter("questionId", answer.getQuestionId())
                    .setParameter("studentId", user.getId())
                    .getSingleResult();
            return eternalQuizAnswer;
        }
        catch (NoResultException e){
            return null;
        }
    }

    private void checkQuizResponse(AnswerResponseDTO answer, EternalQuizAnswer eternalQuizAnswer, AnswerFeedbackDTO response){
        if(!eternalQuizAnswer.getWasSentAsAQuestion()){
            response.addErrorMessage("progmapp.error.eternqlquiz.answer.notaselectedquestion", resultBuilder.translate("progmapp.error.eternqlquiz.answer.notaselectedquestion"));
            return;
        }
        Question question = eternalQuizAnswer.getQuestion();
        Set<PossibleAnswer> possibleAnswers = question.getPossibleAnswers();
        for (PossibleAnswerResponseDTO anAnswer : answer.getAnswers()) {
            //the answer has to be in the possibleAnswers
            if(possibleAnswers.stream().filter(po -> po.getId().equals(anAnswer.getId())).count() == 0){
                response.addErrorMessage("progmapp.error.eternqlquiz.answer.wrongpossibleanswerid", resultBuilder.translate("progmapp.error.eternqlquiz.answer.wrongpossibleanswerid"));
                return;
            }
            //selected answer ids must be real PossibleAnswerValue-s, belonging to this question
            PossibleAnswer possibleAnswer = possibleAnswers.stream().filter(po -> po.getId().equals(anAnswer.getId())).findFirst().get();
            for (String selectedAnswerId : anAnswer.getSelectedAnswerIds()) {
                if(possibleAnswer.getPossibleAnswerValues().stream().filter(pv -> pv.getId().equals(selectedAnswerId)).count() == 0){
                    response.addErrorMessage("progmapp.error.eternqlquiz.answer.wrongpossibleanswervalueid", resultBuilder.translate("progmapp.error.eternqlquiz.answer.wrongpossibleanswervalueid"));
                    return;
                }
            }
        }
    }

    private AnswerEvaluator getAnswerEvaluator(Question question) {
        if(StringUtils.isNotBlank(question.getEvaluationAlogrithm())){
            String algName = AnswerEvaluator.EVAL_ALG_MAP.get(question.getEvaluationAlogrithm());
            if(algName != null){
                return (AnswerEvaluator) context.getBean(algName);
            }
        }
        return (AnswerEvaluator) context.getBean("defaultEvaluator");
    }


    private ActualAnswer createAnswer(AnswerResponseDTO answer, EternalQuizAnswer eternalQuizAnswer) {
        Question question = eternalQuizAnswer.getQuestion();
        ActualAnswer actualAnswer = new ActualAnswer();
        actualAnswer.setQuestion(question);
        actualAnswer.setAnswerText(answer.getAnswerText());
        for (PossibleAnswerResponseDTO anAnswer : answer.getAnswers()) {
            for (String selectedAnswerId : anAnswer.getSelectedAnswerIds()) {
                PossibleAnswerValue selectedAnswerValue = em.find(PossibleAnswerValue.class, selectedAnswerId);
                actualAnswer.addSelectedAnswerValue(selectedAnswerValue);
            }
        }
        eternalQuizAnswer.setLastAnswer(actualAnswer);
        actualAnswer.setEternalQuizAnswer(eternalQuizAnswer);
        return actualAnswer;
    }

    private AnswerFeedbackDTO getResponse(Question question, AnswerEvaulationResult result) {
        AnswerFeedbackDTO ret = new AnswerFeedbackDTO();
        ret.setSuccessFullResult(true);
        ret.setFeedback(question.getExplanationAfter());
        if(result.equals(AnswerEvaulationResult.lateAnswer)){
            ret.setResult(result);
        }
        if(question.getFeedbackType() != null){
            switch(question.getFeedbackType()){
                case trueFalseFeedback:
                    ret.setResult(result);
                    break;
                case showRightAnswer:
                    ret.setResult(result);
                    for (PossibleAnswer possibleAnswer : question.getPossibleAnswers()) {
                        PossibleAnswerDTO possibleAnswerDTO = mapper.map(possibleAnswer, PossibleAnswerDTO.class);
                        ret.addRgithAnswer(possibleAnswerDTO);
                    }
                    break;
                default:
                    break;
            }
        }

        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_ETERNAL_QUIZ_STATISTICS_OF_ANY_STUDENT + "')")
    public EternalQuizStatisticOfStudentsDTO getEternalQuizStatistics(String classId) {
        SchoolClass schoolClass = em.find(SchoolClass.class, classId);
        if(schoolClass == null){
            return new EternalQuizStatisticOfStudentsDTO(resultBuilder.errorResult("progmapp.error.iddoesnotexist", classId, resultBuilder.translate("progmapp.entity.shcoolclass")));
        }
        EternalQuizStatisticOfStudentsDTO ret = new EternalQuizStatisticOfStudentsDTO();
        for (User student : schoolClass.getStudents()) {
            ret.addStatistic(getEternalQuizStatisticsOfStudent(student));
        }
        ret.getStudentStatistics().sort(Comparator.comparingDouble(EternalQuizStatisticDTO::getAchievedPercentage).reversed());
        ret.setSuccessFullResult(true);
        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_OWN_ETERNAL_QUIZ_STATISTICS + "')")
    public EternalQuizStatisticDTO getMyEternalQuizStatistics() {
        User loggedInUser = SecHelper.getLoggedInUser();
        return getEternalQuizStatisticsOfStudent(loggedInUser);
    }

    private EternalQuizStatisticDTO getEternalQuizStatisticsOfStudent(User user) {
        List<EternalQuizAnswer> quizes =
                em.createQuery("select  eq from EternalQuizAnswer eq left join fetch  eq.lastAnswer where eq.student.id = :studentId",
                        EternalQuizAnswer.class)
                        .setParameter("studentId", user.getId())
                        .getResultList();

        int allQuestions = quizes.size();
        int goodAnswers = (int) quizes.stream()
                .filter(q -> q.getHasAnswer())
                .filter(q -> (!q.getLastAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                .count();
        int badAnswers = (int) quizes.stream()
                .filter(q -> q.getHasAnswer())
                .filter(q -> (q.getLastAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                .count();
        Integer targetPercentage = constantService.getConstantValueAsIntegerByKey(ConstantService.KEY_ETERNALQUIZ_TARGET_PERCENTAGE);

        EternalQuizStatisticDTO ret = new EternalQuizStatisticDTO(user.getId(), allQuestions, goodAnswers, badAnswers, targetPercentage);
        return ret;
    }




}
