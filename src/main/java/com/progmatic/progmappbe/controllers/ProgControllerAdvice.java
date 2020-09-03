package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.validation.ValidationErrorDTO;
import com.progmatic.progmappbe.dtos.validation.ValidationErrorResponseDTO;
import com.progmatic.progmappbe.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ProgControllerAdvice {

    Logger LOG = LoggerFactory.getLogger(ProgControllerAdvice.class);

    @ExceptionHandler(value = { UnauthorizedException.class })
    protected ResponseEntity<Object> handleConflict( UnauthorizedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(value=MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponseDTO handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        LOG.warn("Validation failed.", ex);
        ValidationErrorResponseDTO ret = new ValidationErrorResponseDTO();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            ret.addValidatinError(new ValidationErrorDTO(fieldName, errorMessage));
        });
        return ret;
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseBody
    public EntityCreationResult handleNonUniqId(DataIntegrityViolationException ex){
        LOG.error("Data integrity violation", ex);
        EntityCreationResult er = new EntityCreationResult();
        er.setSuccessFullResult(false);
        er.addErrorMessage(ex.getMessage(), ex.getMessage());
        return er;
    }
}
