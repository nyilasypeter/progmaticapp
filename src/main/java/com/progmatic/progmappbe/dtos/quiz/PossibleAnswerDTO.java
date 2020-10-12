/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos.quiz;

import com.progmatic.progmappbe.dtos.BaseEntityDTO;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;

import java.util.ArrayList;
import java.util.List;

/**
 * A Possible answer to a question. 
 * For details @see {@link com.progmatic.progmappbe.entities.Question}
 * @author peti
 */
public class PossibleAnswerDTO extends BaseEntityDTO {
    
    private String textBefore;
    
    private String textAfter;
    
    private PossibleAnswerType type;
        
    private List<PossibleAnswerValueDTO> possibleAnswerValues = new ArrayList<>();

    private Boolean hasImage;

    private Integer order;

    /*Used only if type is soruceCodeToOrder_EvalByRun*/
    private String unitTestCode;

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }

    public PossibleAnswerType getType() {
        return type;
    }

    public void setType(PossibleAnswerType type) {
        this.type = type;
    }

    public List<PossibleAnswerValueDTO> getPossibleAnswerValues() {
        return possibleAnswerValues;
    }

    public void setPossibleAnswerValues(List<PossibleAnswerValueDTO> possibleAnswerValues) {
        this.possibleAnswerValues = possibleAnswerValues;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUnitTestCode() {
        return unitTestCode;
    }

    public void setUnitTestCode(String unitTestCode) {
        this.unitTestCode = unitTestCode;
    }
}
