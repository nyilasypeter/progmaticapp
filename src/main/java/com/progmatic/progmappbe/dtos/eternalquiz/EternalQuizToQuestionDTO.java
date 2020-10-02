package com.progmatic.progmappbe.dtos.eternalquiz;

import java.util.ArrayList;
import java.util.List;

public class EternalQuizToQuestionDTO {

    private String eternalQuizId;
    private List<String> questionIds = new ArrayList<>();

    public String getEternalQuizId() {
        return eternalQuizId;
    }

    public void setEternalQuizId(String eternalQuizId) {
        this.eternalQuizId = eternalQuizId;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }
}
