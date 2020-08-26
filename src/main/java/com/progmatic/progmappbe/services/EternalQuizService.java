package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.EternalQuizDTO;
import com.progmatic.progmappbe.dtos.QuestionDTO;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.helpers.SecHelper;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EternalQuizService {

    public static final int PROBABILITY_TO_CHOOSE_NEW_QUESTION = 40;
    public static final int PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION = 40;
    public static final int PROBABILITY_TO_CHOOSE_WELL_ANSWERED_QUESTION = 20;

    private Random random = new Random();


    @Autowired
    EternalQuizService self;

    @PersistenceContext
    EntityManager em;

    private DozerBeanMapper mapper;

    @Autowired
    public EternalQuizService(DozerBeanMapper mapper) {
        this.mapper = mapper;
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
                em.createQuery("select  eq from EternalQuizAnswer eq left join  eq.actualAnswer where eq.student.id = :studentId",
                        EternalQuizAnswer.class)
                        .setParameter("studentId", loggedInUser.getId())
                        .getResultList();
        EternalQuizAnswer randomAnswer = getRandomAnswer(quizes);
        QuestionDTO qdto = null;
        if (randomAnswer != null) {
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
                    .filter(q -> (q.getActualAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                    .collect(Collectors.toList());
            if (!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        } else if (ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION + PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION + PROBABILITY_TO_CHOOSE_WELL_ANSWERED_QUESTION) {
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> q.getHasAnswer())
                    .filter(q -> (!q.getActualAnswer().getAnswerEvaulationResult().isWrongAnswer()))
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
}
