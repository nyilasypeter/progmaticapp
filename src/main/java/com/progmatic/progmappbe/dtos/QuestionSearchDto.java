package com.progmatic.progmappbe.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.progmatic.progmappbe.helpers.DateHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuestionSearchDto {

    private String questionText;

    @JsonFormat(pattern = DateHelper.DATE_TIME_FORMAT)
    private LocalDateTime uploadTimeFrom;

    @JsonFormat(pattern = DateHelper.DATE_TIME_FORMAT)
    private LocalDateTime uploadTimeTo;

    private String uploader;

    private Boolean isNotInEternalQuiz;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public LocalDateTime getUploadTimeFrom() {
        return uploadTimeFrom;
    }

    public void setUploadTimeFrom(LocalDateTime uploadTimeFrom) {
        this.uploadTimeFrom = uploadTimeFrom;
    }

    public LocalDateTime getUploadTimeTo() {
        return uploadTimeTo;
    }

    public void setUploadTimeTo(LocalDateTime uploadTimeTo) {
        this.uploadTimeTo = uploadTimeTo;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public Boolean getNotInEternalQuiz() {
        return isNotInEternalQuiz;
    }

    public void setNotInEternalQuiz(Boolean notInEternalQuiz) {
        isNotInEternalQuiz = notInEternalQuiz;
    }
}
