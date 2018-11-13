/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos;

/**
 * A Possible value of an answer. 
 * For details @see {@link com.progmatic.progmappbe.entities.Question}
 * @author peti
 */
public class PossibleAnswerValueDTO extends BaseEntityDTO{
    
    private String text;
    
    private Boolean isRightAnswer;

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
    
    
    
    
    
}
