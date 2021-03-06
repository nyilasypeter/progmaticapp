package com.progmatic.progmappbe.dtos.eternalquiz;

import java.util.List;

public class AnswerResponseDTO {
    private String questionId;
    private List<PossibleAnswerResponseDTO> answers;
    private String answerText;

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

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
