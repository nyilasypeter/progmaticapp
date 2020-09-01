package com.progmatic.progmappbe.dtos.validation;

public class ValidationErrorDTO {
    private final String fieldName;

    private final String message;

    public ValidationErrorDTO(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }
    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

}
