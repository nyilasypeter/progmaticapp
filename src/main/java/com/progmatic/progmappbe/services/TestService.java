/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.QuestionDTO;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.exceptions.UnauthorizedException;
import com.progmatic.progmappbe.helpers.SecHelper;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.SerializationUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author peti
 */
@Service
public class TestService {

    private static final int PROBABILITY_TO_CHOOSE_NEW_QUESTION = 40;
    private static final int PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION = 40;
    private static final int PROBABILITY_TO_CHOOSE_WELL_ANSWERED_QUESTION = 20;

    private Random random = new Random();

    private DozerBeanMapper mapper;
    
    @Autowired
    private TestService self;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public TestService(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public EntityCreationResult createQuestion(QuestionDTO q) {
        Question question = mapper.map(q, Question.class);
        for (PossibleAnswer possibleAnswer : question.getPossibleAnswers()) {
            possibleAnswer.setQuestion(question);
            for (PossibleAnswerValue possibleAnswerValue : possibleAnswer.getPossibleAnswerValues()) {
                possibleAnswerValue.setPossibleAnswer(possibleAnswer);
            }
        }
        em.persist(question);
        return new EntityCreationResult(true, question.getId(), null);
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
        if(q==null){
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

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "') or hasAuthority('" + Privilige.PRIV_START_TEST + "')")
    @Transactional
    public QuestionDTO findQuestion(String id) {
        EntityGraph<?> eg = em.createEntityGraph("questionWithPossibleAnswerAndPossibleAnswerValues");
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("javax.persistence.fetchgraph", eg);
        Question q = em.find(Question.class, id, propMap);
        if(q==null){
            return null;
        }
        self.checkHasPermissionToReadQuestion(q);
        QuestionDTO qdto = mapper.map(q, QuestionDTO.class);
        return qdto;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void checkHasPermissionToReadQuestion(Question q) {
        if(!SecHelper.hasAuthority(Privilige.PRIV_CREATE_QUESTION)){
            throw new UnauthorizedException("TODO check that the logged in user has an active test which contains this question");
            //TODO check that the logged in user has an active test which contains this question
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_READ_QUESTION + "')")
    public QuestionDTO getNextEternalQuizQuestion(){
        User loggedInUser = SecHelper.getLoggedInUser();
        List<EternalQuizAnswer> quizes =
                em.createQuery("select  eq from EternalQuizAnswer eq join fetch eq.actualAnswer where eq.student.id = :studentId",
                        EternalQuizAnswer.class)
                .setParameter("studentId", loggedInUser.getId())
                .getResultList();
        EternalQuizAnswer randomAnswer = getRandomAnswer(quizes);
        QuestionDTO qdto = null;
        if(randomAnswer != null){
            Question question = randomAnswer.getQuestion();
            qdto = mapper.map(question, QuestionDTO.class);
        }
        return qdto;
    }

    private EternalQuizAnswer getRandomAnswer(List<EternalQuizAnswer> quizes){
        if(quizes.isEmpty()){
            return null;
        }
        int ran = random.nextInt(100);
        EternalQuizAnswer selectedQuiz = null;
        if(ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION){
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> ! q.getHasAnswer())
                    .collect(Collectors.toList());
            if(!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        }
        else if( ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION + PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION){
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> q.getHasAnswer())
                    .filter(q -> (q.getActualAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                    .collect(Collectors.toList());
            if(!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        }
        else if(ran < PROBABILITY_TO_CHOOSE_NEW_QUESTION + PROBABILITY_TO_CHOOSE_WRONGLY_ANSWERED_QUESTION + PROBABILITY_TO_CHOOSE_WELL_ANSWERED_QUESTION){
            List<EternalQuizAnswer> notAnsweredOnes = quizes.stream()
                    .filter(q -> q.getHasAnswer())
                    .filter(q -> (!q.getActualAnswer().getAnswerEvaulationResult().isWrongAnswer()))
                    .collect(Collectors.toList());
            if(!notAnsweredOnes.isEmpty()) {
                selectedQuiz = notAnsweredOnes.get(random.nextInt(notAnsweredOnes.size()));
            }
        }
        if(selectedQuiz == null){
            selectedQuiz = quizes.get(random.nextInt(quizes.size()));
        }
        return selectedQuiz;
    }
}
