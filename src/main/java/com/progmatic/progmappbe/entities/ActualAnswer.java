/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author peti
 */
@Entity
public class ActualAnswer extends BaseEntity{
    
    private String answerText;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private ActualTest actualTest;
    
    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "actualAnswer")
    private Set<ActualAnswerValue> selectedAnswerValues = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
        
    private Integer actualScore;

    @Enumerated(EnumType.STRING)
    private AnswerEvaulationResult answerEvaulationResult;

    @ManyToOne(fetch = FetchType.LAZY)
    private EternalQuizAnswer eternalQuizAnswer;

    public void addSelectedAnswerValue(ActualAnswerValue possibleAnswerValue){
        selectedAnswerValues.add(possibleAnswerValue);
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public ActualTest getActualTest() {
        return actualTest;
    }

    public void setActualTest(ActualTest actualTest) {
        this.actualTest = actualTest;
    }

    public Set<ActualAnswerValue> getSelectedAnswerValues() {
        return selectedAnswerValues;
    }

    public void setSelectedAnswerValues(Set<ActualAnswerValue> selectedAnswerValues) {
        this.selectedAnswerValues = selectedAnswerValues;
    }

    public Integer getActualScore() {
        return actualScore;
    }

    public void setActualScore(Integer actualScore) {
        this.actualScore = actualScore;
    }

    public AnswerEvaulationResult getAnswerEvaulationResult() {
        return answerEvaulationResult;
    }

    public void setAnswerEvaulationResult(AnswerEvaulationResult answerEvaulationResult) {
        this.answerEvaulationResult = answerEvaulationResult;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public EternalQuizAnswer getEternalQuizAnswer() {
        return eternalQuizAnswer;
    }

    public void setEternalQuizAnswer(EternalQuizAnswer eternalQuizAnswer) {
        this.eternalQuizAnswer = eternalQuizAnswer;
    }
}
