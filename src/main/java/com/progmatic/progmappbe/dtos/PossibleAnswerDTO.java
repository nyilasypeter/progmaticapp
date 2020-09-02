/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos;

import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import java.util.ArrayList;
import java.util.List;

/**
 * A Possible answer to a question. 
 * For details @see {@link com.progmatic.progmappbe.entities.Question}
 * @author peti
 */
public class PossibleAnswerDTO extends BaseEntityDTO{
    
    private String textBefore;
    
    private String textAfter;
    
    private PossibleAnswerType type;
        
    private List<PossibleAnswerValueDTO> possibleAnswerValues = new ArrayList<>();

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
    
    
    
}
