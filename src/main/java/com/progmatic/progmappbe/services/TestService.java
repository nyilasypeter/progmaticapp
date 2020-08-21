/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.QuestionDTO;
import com.progmatic.progmappbe.entities.PossibleAnswer;
import com.progmatic.progmappbe.entities.PossibleAnswerValue;
import com.progmatic.progmappbe.entities.Privilige;
import com.progmatic.progmappbe.entities.Question;
import com.progmatic.progmappbe.exceptions.UnauthorizedException;
import com.progmatic.progmappbe.helpers.SecHelper;
import java.util.HashMap;
import java.util.Map;
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

    @Autowired
    DozerBeanMapper mapper;
    
    @Autowired
    TestService self;

    @PersistenceContext
    EntityManager em;

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_QUESTION + "')")
    @Transactional
    public String createQuestion(QuestionDTO q) {
        Question question = mapper.map(q, Question.class);
        for (PossibleAnswer possibleAnswer : question.getPossibleAnswers()) {
            possibleAnswer.setQuestion(question);
            for (PossibleAnswerValue possibleAnswerValue : possibleAnswer.getPossibleAnswerValues()) {
                possibleAnswerValue.setPossibleAnswer(possibleAnswer);
            }
        }
        em.persist(question);
        return question.getId();
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
}
