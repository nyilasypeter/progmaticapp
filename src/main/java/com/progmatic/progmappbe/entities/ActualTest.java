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

/**
 * An actual test that belongs to a student and contains the student's answers.
 * @author peti
 */
@Entity
public class ActualTest extends BaseEntity{
    
    @ManyToOne
    private Test test;
    
    @ManyToOne
    private User student;
    
    @Enumerated(EnumType.STRING)
    ActualTestStatus status;

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public ActualTestStatus getStatus() {
        return status;
    }

    public void setStatus(ActualTestStatus status) {
        this.status = status;
    }
   
}
