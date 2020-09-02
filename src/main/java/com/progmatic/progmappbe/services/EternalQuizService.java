package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.quizresponse.AnswerFeedbackDTO;
import com.progmatic.progmappbe.dtos.quizresponse.AnswerResponseDTO;
import com.progmatic.progmappbe.dtos.quizresponse.PossibleAnswerResponseDTO;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.helpers.SecHelper;
import com.progmatic.progmappbe.anwerevaluator.AnswerEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public EternalQuizService(DozerBeanMapper mapper, ApplicationContext context, ConstantService constantService) {
        this.mapper = mapper;
        this.context = context;
        this.constantService = constantService;
    }


    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public EntityCreationResult createEternalQuiz(EternalQuizDTO edto){
        EternalQuiz eq = new EternalQuiz();
        EternalQuiz eternalQuiz = em.find(EternalQuiz.class, edto.getId());
        if(eternalQuiz != null){
            return new EntityCreationResult(false, null, String.format("EternalQuiz with this id: %s already exists.", edto.getId()) );
        }
        eq.setId(edto.getId());
        StringBuilder errors = new StringBuilder("");
        for (String questionId : edto.getQuestionIds()) {
            Question question = em.find(Question.class, questionId);
            if(question != null){
                eq.addQuestion(question);
            }
            else{
                errors.append(String.format("Question with id: %s does not exist.", questionId));
            }

        }
        em.persist(eq);
        return new EntityCreationResult(true, eq.getId(), errors.toString());
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CRUD_ETERNAL_QUIZ + "')")
    public BasicResult assignQuestionToEternalQuiz(String eternqlQizId, String questionId){
        EternalQuiz eternalQuiz = em.find(EternalQuiz.class, eternqlQizId);
        Question question = em.find(Question.class, questionId);
        if(eternalQuiz == null || question == null){
            return new BasicResult(false, "EternalQiz or question does not exist");
        }
        List resultList = em.createQuery("select e from EternalQuiz e inner join e.questions q where e.id = :eQId and q.id = :qId")
                .setParameter("eQId", eternqlQizId)
                .setParameter("qId", questionId)
                .getResultList();
        if(!resultList.isEmpty()){
            return new BasicResult(false, "EternalQuiz already conatains this question.");
        }
        eternalQuiz.addQuestion(question);
        self.fillEternalQuizAnswers(eternalQuiz, question);
        return new BasicResult(true);
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
        if(eternalQuiz == null || schoolClass == null){
            return new BasicResult(false, "EternalQiz of schoolClass does not exist");
        }
        List resultList = em.createQuery("select e from EternalQuiz e inner join e.schoolClasses c where e.id = :eQId and c.id = :classId")
                .setParameter("eQId", eternqlQizId)
                .setParameter("classId", schoolClassId)
                .getResultList();
        if(!resultList.isEmpty()){
            return new BasicResult(false, "EternalQuiz already conatains this class.");
        }
        eternalQuiz.addSchoolClass(schoolClass);
        self.fillAllEternalQuizAnswers(eternalQuiz);
        return new BasicResult(true);
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

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "')")
    public QuestionDTO getNextEternalQuizQuestion() {
        User loggedInUser = SecHelper.getLoggedInUser();
        List<EternalQuizAnswer> quizes =
                em.createQuery("select  eq from EternalQuizAnswer eq left join  eq.lastAnswer where eq.student.id = :studentId",
                        EternalQuizAnswer.class)
                        .setParameter("studentId", loggedInUser.getId())
                        .getResultList();
        EternalQuizAnswer randomAnswer = getRandomAnswer(quizes);
        QuestionDTO qdto = null;
        if (randomAnswer != null) {
            randomAnswer.setTimeOfLastAccess(System.currentTimeMillis());
            randomAnswer.setWasSentAsAQuestion(true);
            Question question = randomAnswer.getQuestion();
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
            return  new AnswerFeedbackDTO(false, "No EternalQuizAnswer exists for this user and this question.");
        }
        String errorString = checkQuizResponse(answer, eternalQuizAnswer);
        if(StringUtils.isNotBlank(errorString)){
            return new AnswerFeedbackDTO(false, errorString);
        }
        eternalQuizAnswer.setWasSentAsAQuestion(false);
        Question question = eternalQuizAnswer.getQuestion();
        if(question.getAnswerTimeInSec() != null) {
            long answerTimeInSec = (System.currentTimeMillis() - eternalQuizAnswer.getTimeOfLastAccess()) / 1000;
            if(answerTimeInSec > question.calculatedAnswerTimeInSec()){
                AnswerFeedbackDTO answerFeedbackDTO = new AnswerFeedbackDTO();
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

    private String checkQuizResponse(AnswerResponseDTO answer, EternalQuizAnswer eternalQuizAnswer){
        if(!eternalQuizAnswer.getWasSentAsAQuestion()){
            return  "This EternalQuizAnswer was not selected as question (maybe already answered?).";
        }
        Question question = eternalQuizAnswer.getQuestion();
        Set<PossibleAnswer> possibleAnswers = question.getPossibleAnswers();
        for (PossibleAnswerResponseDTO anAnswer : answer.getAnswers()) {
            //the answer has to be in the possibleAnswers
            if(possibleAnswers.stream().filter(po -> po.getId().equals(anAnswer.getId())).count() == 0){
                return "ids of answers must be valid PossibleAnswerResponse ids for this eternalQuiz.";
            }
            //selected answer ids must be real PossibleAnswerValue-s, belonging to this question
            PossibleAnswer possibleAnswer = possibleAnswers.stream().filter(po -> po.getId().equals(anAnswer.getId())).findFirst().get();
            for (String selectedAnswerId : anAnswer.getSelectedAnswerIds()) {
                if(possibleAnswer.getPossibleAnswerValues().stream().filter(pv -> pv.getId().equals(selectedAnswerId)).count() == 0){
                    return "selectedAnswerIds must be valid PossibleAnswerValue ids for this eternalQuiz";
                }
            }
        }
        return null;
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
            return new EternalQuizStatisticOfStudentsDTO(false, "SchoolClass does not exist.");
        }
        EternalQuizStatisticOfStudentsDTO ret = new EternalQuizStatisticOfStudentsDTO();
        for (User student : schoolClass.getStudents()) {
            ret.addStatistic(getEternalQuizStatisticsOfStudent(student));
        }
        ret.getStudentStatistics().sort(Comparator.comparingDouble(EternalQuizStatisticDTO::getAchievedPercentage).reversed());
        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_OWN_ETERNAL_QUIZ_STATISTICS + "')")
    public EternalQuizStatisticDTO getMyEternalQuizStatistics() {
        User loggedInUser = SecHelper.getLoggedInUser();
        return getEternalQuizStatisticsOfStudent(loggedInUser);
    }

    private EternalQuizStatisticDTO getEternalQuizStatisticsOfStudent(User user) {
        List<EternalQuizAnswer> quizes =
                em.createQuery("select  eq from EternalQuizAnswer eq left join  eq.lastAnswer where eq.student.id = :studentId",
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
