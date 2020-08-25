package com.progmatic.progmappbe.dtos;

import org.apache.commons.lang3.StringUtils;

public class BasicResult {

    private boolean successfullCreation;
    private String errorMessage;

    public BasicResult(boolean successfullCreation, String errorMessage) {
        this.successfullCreation = successfullCreation;
        this.errorMessage = StringUtils.isBlank(errorMessage) ? null : errorMessage;
    }

    public BasicResult(boolean successfullCreation) {
        this.successfullCreation = successfullCreation;
    }

    public boolean isSuccessfullCreation() {
        return successfullCreation;
    }

    public void setSuccessfullCreation(boolean successfullCreation) {
        this.successfullCreation = successfullCreation;
    }
}
