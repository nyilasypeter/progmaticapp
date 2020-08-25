package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.EternalQuizDTO;
import com.progmatic.progmappbe.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@Service
public class EternalQuizService {

    @Autowired
    EternalQuizService self;

    @PersistenceContext
    EntityManager em;

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
}
