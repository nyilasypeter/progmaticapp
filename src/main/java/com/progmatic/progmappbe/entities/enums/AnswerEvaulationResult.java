package com.progmatic.progmappbe.entities.enums;

public enum AnswerEvaulationResult {
    rightAnswer, falseAnswer, partiallyRightAnswer, lateAnswer;

    public boolean isWrongAnswer(){
        return this.equals(falseAnswer) || this.equals(partiallyRightAnswer) || this.equals(lateAnswer);
    }
}
