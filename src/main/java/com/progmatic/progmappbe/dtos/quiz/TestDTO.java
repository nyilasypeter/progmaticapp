/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos.quiz;

import com.progmatic.progmappbe.dtos.BaseEntityDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionInTestDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * A test containing questions and their possible answers.
 * @author peti
 */
public class TestDTO extends BaseEntityDTO {
    
    private String name;
    
    private String description;
    
    private List<QuestionInTestDTO> questionInTests = new ArrayList<>();
    
    private Boolean showScores = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionInTestDTO> getQuestionInTests() {
        return questionInTests;
    }

    public void setQuestionInTests(List<QuestionInTestDTO> questionInTests) {
        this.questionInTests = questionInTests;
    }

    public Boolean getShowScores() {
        return showScores;
    }

    public void setShowScores(Boolean showScores) {
        this.showScores = showScores;
    }

   
}
