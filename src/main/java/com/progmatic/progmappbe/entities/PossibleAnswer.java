/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A Possible answer to a question. 
 * For details @see {@link com.progmatic.progmappbe.entities.Question}
 * @author peti
 */
@Entity
public class PossibleAnswer extends BaseEntity{
    
    private String textBefore;
    
    private String textAfter;
    
    @Enumerated(EnumType.STRING)
    private PossibleAnswerType type;

        
    @OneToMany(mappedBy = "possibleAnswer", cascade = {CascadeType.PERSIST})
    private Set<PossibleAnswerValue> possibleAnswerValues = new HashSet<>();
    
    @ManyToOne
    private Question question;

    private Boolean hasImage = false;

    @Column(name = "order_nr")
    private Integer order;

    /*Used only if type is soruceCodeToOrder_EvalByRun*/
    @Lob
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

    public Set<PossibleAnswerValue> getPossibleAnswerValues() {
        return possibleAnswerValues;
    }

    public void setPossibleAnswerValues(Set<PossibleAnswerValue> possibleAnswerValues) {
        this.possibleAnswerValues = possibleAnswerValues;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
