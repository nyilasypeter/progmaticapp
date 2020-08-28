package com.progmatic.progmappbe.dtos;


public class EternalQuizStatisticDTO {

    private final String userId;
    private final Integer nrOfAllQuestions;
    private final Integer nrOfRightAnswers;
    private final Integer nrOfBadAnswers;
    private final Integer targetPercentage;

    public EternalQuizStatisticDTO(String userId, Integer nrOfAllQuestions, Integer nrOfRightAnswers, Integer nrOfBadAnswers, Integer targetPercentage) {
        this.userId = userId;
        this.nrOfAllQuestions = nrOfAllQuestions;
        this.nrOfRightAnswers = nrOfRightAnswers;
        this.nrOfBadAnswers = nrOfBadAnswers;
        this.targetPercentage = targetPercentage;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getNrOfAllQuestions() {
        return nrOfAllQuestions;
    }

    public Integer getNrOfRightAnswers() {
        return nrOfRightAnswers;
    }

    public Integer getNrOfBadAnswers() {
        return nrOfBadAnswers;
    }

    public Integer getTargetPercentage() {
        return targetPercentage;
    }

    public Double getAchievedPercentage(){
        if(nrOfAllQuestions == null || nrOfRightAnswers == null){
            return null;
        }
        if(nrOfAllQuestions == 0){
            return 0d;
        }
        return nrOfRightAnswers.doubleValue() / nrOfAllQuestions.doubleValue() * 100d;
    }

}
