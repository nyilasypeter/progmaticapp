package com.progmatic.progmappbe.dtos.quizresponse;

import java.util.List;

public class PossibleAnswerResponseDTO {
    private String id;
    private List<String> selectedAnswerIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSelectedAnswerIds() {
        return selectedAnswerIds;
    }

    public void setSelectedAnswerIds(List<String> selectedAnswerIds) {
        this.selectedAnswerIds = selectedAnswerIds;
    }
}
