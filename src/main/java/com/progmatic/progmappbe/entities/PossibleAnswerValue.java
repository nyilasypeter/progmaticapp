/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * A Possible value of an answer. 
 * For details @see {@link com.progmatic.progmappbe.entities.Question}
 * @author peti
 */
@Entity
public class PossibleAnswerValue extends BaseEntity{
    
    private String text;
    
    private Boolean isRightAnswer;

    /*
     * Used if PossibleAnswerType is soruceCodeToOrder_EvalByCompare
     * */
    private Integer rightOrder;

    /*
    * Used only if PossibleAnswerType is soruceCodeToOrder_EvalByRun
    * */
    private String sourceCode;

    /*
     * Used only if PossibleAnswerType is soruceCodeToOrder_EvalByRun
     * */
    private String unitTestCode;
    
    @ManyToOne
    private PossibleAnswer possibleAnswer;

    @OneToMany(mappedBy = "possibleAnswerValue")
    private Set<ActualAnswerValue> actualAnswers = new HashSet<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsRightAnswer() {
        return isRightAnswer;
    }

    public void setIsRightAnswer(Boolean isRightAnswer) {
        this.isRightAnswer = isRightAnswer;
    }

    public PossibleAnswer getPossibleAnswer() {
        return possibleAnswer;
    }

    public void setPossibleAnswer(PossibleAnswer possibleAnswer) {
        this.possibleAnswer = possibleAnswer;
    }

    public Set<ActualAnswerValue> getActualAnswers() {
        return actualAnswers;
    }

    public void setActualAnswers(Set<ActualAnswerValue> actualAnswers) {
        this.actualAnswers = actualAnswers;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getUnitTestCode() {
        return unitTestCode;
    }

    public void setUnitTestCode(String unitTestCode) {
        this.unitTestCode = unitTestCode;
    }

    public Integer getRightOrder() {
        return rightOrder;
    }

    public void setRightOrder(Integer rightOrder) {
        this.rightOrder = rightOrder;
    }
}
