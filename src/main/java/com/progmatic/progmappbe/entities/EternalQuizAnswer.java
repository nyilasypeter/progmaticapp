package com.progmatic.progmappbe.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    User student;

    @ManyToOne(fetch = FetchType.LAZY)
    Question question;

    @OneToOne
    ActualAnswer actualAnswer;

    /**
     * false if it refers to an unanswered question,
     * See class comment
     */
    @NotNull
    private Boolean hasAnswer;

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

    public ActualAnswer getActualAnswer() {
        return actualAnswer;
    }

    public void setActualAnswer(ActualAnswer actualAnswer) {
        this.actualAnswer = actualAnswer;
    }

    public Boolean getHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(Boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }
}
