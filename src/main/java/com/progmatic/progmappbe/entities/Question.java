/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;

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
@Entity
@NamedEntityGraphs({
    @NamedEntityGraph(name = "questionWithPossibleAnswerAndPossibleAnswerValues",
            attributeNodes = {
                @NamedAttributeNode(
                        value = "possibleAnswers", subgraph = "question.possAnswers")
            },
            subgraphs = {
                @NamedSubgraph(
                        name = "question.possAnswers",
                        attributeNodes = @NamedAttributeNode(value = "possibleAnswerValues"))
            }
            
    )
})
public class Question extends BaseEntity {
    @Lob
    private String text;
        
    @Lob
    @Column(length=100000)
    private byte[] image;   
    
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST})
    private Set<PossibleAnswer> possibleAnswers = new HashSet<>();
    
    @OneToMany(mappedBy = "question")
    private Set<QuestionInTest> questionInTests = new HashSet<>();

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

    public Set<PossibleAnswer> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(Set<PossibleAnswer> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }
    
    public void addPossibleAnswer(PossibleAnswer pa){
        this.possibleAnswers.add(pa);
    }

    public Set<QuestionInTest> getQuestionInTests() {
        return questionInTests;
    }

    public void setQuestionInTests(Set<QuestionInTest> questionInTests) {
        this.questionInTests = questionInTests;
    }
    
}
