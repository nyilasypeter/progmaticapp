package com.progmatic.progmappbe.dtos;

import java.util.Set;

public class EternalQuizDTO {
    private String id;
    private Set<String> questionIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(Set<String> questionIds) {
        this.questionIds = questionIds;
    }
}
