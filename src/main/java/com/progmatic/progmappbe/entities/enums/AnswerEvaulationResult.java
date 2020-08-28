package com.progmatic.progmappbe.entities.enums;

public enum AnswerEvaulationResult {
    rightAnswer, falseAnswer, partiallyRightAnswer, lateAnswer;

    public boolean isWrongAnswer(){
        return this.equals(falseAnswer) || this.equals(partiallyRightAnswer) || this.equals(lateAnswer);
    }

    public AnswerEvaulationResult and(AnswerEvaulationResult res){
        if(this.equals(falseAnswer) || res.equals(falseAnswer)) return falseAnswer;
        if(this.equals(lateAnswer) || res.equals(lateAnswer)) return lateAnswer;
        if(this.equals(rightAnswer) && res.equals(rightAnswer)) return rightAnswer;
        return partiallyRightAnswer;
    }
}
