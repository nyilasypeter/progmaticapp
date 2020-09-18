/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
    
    @ManyToOne
    private PossibleAnswer possibleAnswer;

    @ManyToMany(mappedBy = "selectedAnswerValues")
    private Set<ActualAnswer> actualAnswers = new HashSet<>();

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

    public Set<ActualAnswer> getActualAnswers() {
        return actualAnswers;
    }

    public void setActualAnswers(Set<ActualAnswer> actualAnswers) {
        this.actualAnswers = actualAnswers;
    }
}
