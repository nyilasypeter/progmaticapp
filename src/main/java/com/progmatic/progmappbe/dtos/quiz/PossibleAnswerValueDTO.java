/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos.quiz;

import com.progmatic.progmappbe.dtos.BaseEntityDTO;

/**
 * A Possible value of an answer. 
 * For details @see {@link com.progmatic.progmappbe.entities.Question}
 * @author peti
 */
public class PossibleAnswerValueDTO extends BaseEntityDTO {
    
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

    public Boolean getRightAnswer() {
        return isRightAnswer;
    }

    public void setRightAnswer(Boolean rightAnswer) {
        isRightAnswer = rightAnswer;
    }

    public Integer getRightOrder() {
        return rightOrder;
    }

    public void setRightOrder(Integer rightOrder) {
        this.rightOrder = rightOrder;
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
}
