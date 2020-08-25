/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a question of a test.
 * A question has one or more PossibleAnswers, 
 * each PossibleAnswer having zero or more PossibleAnswerValues.
 * E.g. 
 * Suppose that the Question is:  <b>What are the two favourite foods of Winnie the Pooh?</b><br/>
 * And suppose that we want the user to be able to select two
 * different answers from two drop-down (each containing foods).<br/>
 * Then our entity structure will look like this:<br/><br/>
 * Question - What are the two favourite foods of Winnie the Pooh?<br/>
 * <ul>
 * <li>
 * 1st Possible answer  
 * <ul>
 * <li>Possible answer value - bread - wroing answer</li>
 * <li>Possible answer value - blueberry - wrong answer</li>
 * <li>Possible answer value - honey - right answer</li>
 * <li>Possible answer value - beeer - wrong answer</li>
 * </ul>
 * </li>
 * <li>
 * 2nd Possible answer  
 * <ul>
 * <li>Possible answer value - bread - wroing answer</li>
 * <li>Possible answer value - blueberry - wrong answer</li>
 * <li>Possible answer value - honey - right answer</li>
 * <li>Possible answer value - beeer - wrong answer</li>
 * </ul>
 * </li>
 * </ul>
 * Thus we can see that both the first and the second favourite food of Winnie the Pooh is honey.
 * @author peti
 */
public class QuestionDTO extends BaseEntityDTO {
    private String text;
        
    private byte[] image;

    private Integer answerTimeInSec;
    
    private List<PossibleAnswerDTO> possibleAnswers = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<PossibleAnswerDTO> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<PossibleAnswerDTO> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public Integer getAnswerTimeInSec() {
        return answerTimeInSec;
    }

    public void setAnswerTimeInSec(Integer answerTimeInSec) {
        this.answerTimeInSec = answerTimeInSec;
    }
}
