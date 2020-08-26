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
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
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
 * @author peti
 */
@Service
public class TestService {

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
        StringBuilder errorMessages = new StringBuilder("");
        for (PossibleAnswer possibleAnswer : question.getPossibleAnswers()) {
            possibleAnswer.setQuestion(question);
            guessAnswerTypeIfNeeded(possibleAnswer);
            checkPossibleAnswer(possibleAnswer, errorMessages);
            for (PossibleAnswerValue possibleAnswerValue : possibleAnswer.getPossibleAnswerValues()) {
                possibleAnswerValue.setPossibleAnswer(possibleAnswer);
            }
        }
        if(errorMessages.length() > 0){
            return new EntityCreationResult(false, null, errorMessages.toString());
        }
        else {
            em.persist(question);
            return new EntityCreationResult(true, question.getId(), null);
        }
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

    private void checkPossibleAnswer(PossibleAnswer possibleAnswer, StringBuilder erorMsgs) {
        switch (possibleAnswer.getType()) {
            case trueFalseCheckbox:
                if (possibleAnswer.getPossibleAnswerValues().size() != 1) {
                    erorMsgs.append("If type of a possible answers is trueFalseCheckbox, then it must contain exactly one PossibleAnswerValue");
                }
                break;
            case radioButtons:
            case dropdown:
                if (possibleAnswer.getPossibleAnswerValues().stream().filter(pav -> pav.getIsRightAnswer()).count()  != 1) {
                    erorMsgs.append("If type of a possible answers is radioButtons or dropdown, then it must contain exactly one right PossibleAnswerValue");
                }
                break;
            case longText:
            case shortText:
                if (!possibleAnswer.getPossibleAnswerValues().isEmpty()) {
                    erorMsgs.append("If type of a possible answers is longText or shortText, then it should not contain any PossibleAnswerValue");
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


}
