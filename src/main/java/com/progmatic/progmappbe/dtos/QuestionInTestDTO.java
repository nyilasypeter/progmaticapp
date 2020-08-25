/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos;

import com.progmatic.progmappbe.entities.enums.ScoreMechanism;


/**
 * A question in a test with score and order number.
 * @author peti
 */
public class QuestionInTestDTO extends BaseEntityDTO{
    
    private int orderNr;
    
    private int score;
    
    private ScoreMechanism scoreMechanism = ScoreMechanism.ALL_OR_NOTHING;
    
    private QuestionDTO question;

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

    public ScoreMechanism getScoreMechanism() {
        return scoreMechanism;
    }

    public void setScoreMechanism(ScoreMechanism scoreMechanism) {
        this.scoreMechanism = scoreMechanism;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }
    

   
}
