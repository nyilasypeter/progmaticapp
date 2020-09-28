package com.progmatic.progmappbe.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ActualAnswerValue extends BaseEntity {

    @ManyToOne
    private ActualAnswer actualAnswer;

    @ManyToOne
    private PossibleAnswerValue possibleAnswerValue;

    private Integer selectedOrder;

    public ActualAnswer getActualAnswer() {
        return actualAnswer;
    }

    public void setActualAnswer(ActualAnswer actualAnswer) {
        this.actualAnswer = actualAnswer;
    }

    public PossibleAnswerValue getPossibleAnswerValue() {
        return possibleAnswerValue;
    }

    public void setPossibleAnswerValue(PossibleAnswerValue possibleAnswerValue) {
        this.possibleAnswerValue = possibleAnswerValue;
    }

    public Integer getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Integer selectedOrder) {
        this.selectedOrder = selectedOrder;
    }
}
