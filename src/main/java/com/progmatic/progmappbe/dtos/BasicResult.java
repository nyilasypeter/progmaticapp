package com.progmatic.progmappbe.dtos;

import org.apache.commons.lang3.StringUtils;

public class BasicResult {

    private boolean successFullResult;
    private String errorMessage;

    public BasicResult(boolean successFullResult, String errorMessage) {
        this.successFullResult = successFullResult;
        this.errorMessage = StringUtils.isBlank(errorMessage) ? null : errorMessage;
    }

    public BasicResult(boolean successfullCreation) {
        this.successFullResult = successfullCreation;
    }

    public boolean isSuccessFullResult() {
        return successFullResult;
    }

    public void setSuccessFullResult(boolean successFullResult) {
        this.successFullResult = successFullResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
