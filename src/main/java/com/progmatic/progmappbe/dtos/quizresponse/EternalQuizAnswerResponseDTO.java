package com.progmatic.progmappbe.dtos.quizresponse;

import java.util.List;

public class EternalQuizAnswerResponseDTO {
    private String questionId;
    private List<PossibleAnswerResponseDTO> answers;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List<PossibleAnswerResponseDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<PossibleAnswerResponseDTO> answers) {
        this.answers = answers;
    }
}
