package com.progmatic.progmappbe.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * The EternalQuizAnswer table contains all questions that a student might get
 * at the current period.
 * It does not only contains questions that the student already answered,
 * but also questions not yet answered.
 * This results in redundantly stored data, but in return
 * greatly simplifies geting a next question from the
 * questions belonging to the eternal quiz of a student.
 */
@Entity
@Table(indexes = {@Index(name = "studentIdIdx", columnList = "student_id")})
public class EternalQuizAnswer extends BaseEntity {

    /* When a question is selected t be shown to the student, this field is set to currentTimeMillis*/
    private Long timeOfLastAccess;

    /* Set to true, when this record is selected to be the next question of the student,
    * set back to false when it is answered */
    private Boolean wasSentAsAQuestion = false;

    private Integer nrOfTrials = 0;

    private Integer nrOfGoodTrials = 0;

    private Integer nrOfBadTrials = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    /* The last answer, that the student submitted. */
    @OneToOne
    private ActualAnswer lastAnswer;

    /* All the answers the user ever submitted. */
    @OneToMany(mappedBy = "eternalQuizAnswer")
    private Set<ActualAnswer> allAnswers = new HashSet<>();

    /**
     * false if it refers to an unanswered question,
     * See class comment
     */
    @NotNull
    private Boolean hasAnswer;

    public void oneGoodTrial(){
        nrOfTrials++;
        nrOfGoodTrials++;
    }

    public void oneBadTrial(){
        nrOfTrials++;
        nrOfBadTrials++;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ActualAnswer getLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer(ActualAnswer actualAnswer) {
        this.lastAnswer = actualAnswer;
    }

    public Boolean getHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(Boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    public Integer getNrOfTrials() {
        return nrOfTrials;
    }

    public void setNrOfTrials(Integer nrOfTrials) {
        this.nrOfTrials = nrOfTrials;
    }

    public Integer getNrOfGoodTrials() {
        return nrOfGoodTrials;
    }

    public void setNrOfGoodTrials(Integer nrOfGoodTrials) {
        this.nrOfGoodTrials = nrOfGoodTrials;
    }

    public Integer getNrOfBadTrials() {
        return nrOfBadTrials;
    }

    public void setNrOfBadTrials(Integer nrOfBadTrials) {
        this.nrOfBadTrials = nrOfBadTrials;
    }

    public Long getTimeOfLastAccess() {
        return timeOfLastAccess;
    }

    public void setTimeOfLastAccess(Long timeOfLastAccess) {
        this.timeOfLastAccess = timeOfLastAccess;
    }

    public Boolean getWasSentAsAQuestion() {
        return wasSentAsAQuestion;
    }

    public void setWasSentAsAQuestion(Boolean wasSentAsAQuestion) {
        this.wasSentAsAQuestion = wasSentAsAQuestion;
    }

    public void addToAllAnswers(ActualAnswer actualAnswer){
        allAnswers.add(actualAnswer);
    }

    public Set<ActualAnswer> getAllAnswers() {
        return allAnswers;
    }

    public void setAllAnswers(Set<ActualAnswer> allAnswers) {
        this.allAnswers = allAnswers;
    }
}
