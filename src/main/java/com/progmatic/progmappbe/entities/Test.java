/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A test containing questions and their possible answers.
 * @author peti
 */
@Entity
public class Test extends BaseEntity{
    
    private String name;
    
    private String description;
    
    @OneToMany(mappedBy = "test")
    private Set<QuestionInTest> questionInTests = new HashSet<>();
    
    @OneToMany(mappedBy = "test")
    private Set<ActualTest> actualTests = new HashSet<>();
    
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

    public Set<QuestionInTest> getQuestionInTests() {
        return questionInTests;
    }

    public void setQuestionInTests(Set<QuestionInTest> questionInTests) {
        this.questionInTests = questionInTests;
    }

    public Set<ActualTest> getActualTests() {
        return actualTests;
    }

    public void setActualTests(Set<ActualTest> actualTests) {
        this.actualTests = actualTests;
    }

    public Boolean getShowScores() {
        return showScores;
    }

    public void setShowScores(Boolean showScores) {
        this.showScores = showScores;
    }
    
}
