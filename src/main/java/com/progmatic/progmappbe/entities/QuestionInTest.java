/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A question in a test with score and order number.
 * @author peti
 */
@Entity
public class QuestionInTest extends BaseEntity{
    
    private int orderNr;
    
    private int score;
    
    @Enumerated(EnumType.STRING)
    private ScoreMechanism scoreMechanism = ScoreMechanism.ALL_OR_NOTHING;
    
    @ManyToOne()
    private Question question;
    
    @ManyToOne
    private Test test;

    public int getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(int orderNr) {
        this.orderNr = orderNr;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public ScoreMechanism getScoreMechanism() {
        return scoreMechanism;
    }

    public void setScoreMechanism(ScoreMechanism scoreMechanism) {
        this.scoreMechanism = scoreMechanism;
    }
    
    
    
}
