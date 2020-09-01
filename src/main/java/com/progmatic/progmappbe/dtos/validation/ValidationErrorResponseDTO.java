package com.progmatic.progmappbe.dtos.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponseDTO {

    private List<ValidationErrorDTO> validationErrors = new ArrayList<>();

    public void addValidatinError(ValidationErrorDTO ve){
        validationErrors.add(ve);
    }

    public List<ValidationErrorDTO> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationErrorDTO> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
