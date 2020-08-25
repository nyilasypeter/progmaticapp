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
    
    @OneToMany(cascade = {CascadeType.PERSIST})
    private Set<PossibleAnswerValue> selectedAnswerValues = new HashSet<>();
        
    private Integer actualScore;

    private AnswerEvaulationResult answerEvaulationResult;

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

    public Set<PossibleAnswerValue> getSelectedAnswerValues() {
        return selectedAnswerValues;
    }

    public void setSelectedAnswerValues(Set<PossibleAnswerValue> selectedAnswerValues) {
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
}
