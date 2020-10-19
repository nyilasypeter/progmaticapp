package com.progmatic.progmappbe.dtos.quiz;

import com.progmatic.progmappbe.dtos.BaseEntityRequestDto;
import com.progmatic.progmappbe.entities.enums.FeedbackType;

import javax.validation.constraints.NotEmpty;

public class OrderLinesQuestionRequestDTO extends BaseEntityRequestDto {

    private String text;

    private String adminDescription;

    private String explanationAfter;

    private Integer answerTimeInSec;

    private FeedbackType feedbackType;

    @NotEmpty
    private String code;

    private String unitTest;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAdminDescription() {
        return adminDescription;
    }

    public void setAdminDescription(String adminDescription) {
        this.adminDescription = adminDescription;
    }

    public String getExplanationAfter() {
        return explanationAfter;
    }

    public void setExplanationAfter(String explanationAfter) {
        this.explanationAfter = explanationAfter;
    }

    public Integer getAnswerTimeInSec() {
        return answerTimeInSec;
    }

    public void setAnswerTimeInSec(Integer answerTimeInSec) {
        this.answerTimeInSec = answerTimeInSec;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnitTest() {
        return unitTest;
    }

    public void setUnitTest(String unitTest) {
        this.unitTest = unitTest;
    }
}
