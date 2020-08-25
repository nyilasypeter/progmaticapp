package com.progmatic.progmappbe.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class EternalQuiz extends BaseEntity{


    @ManyToMany
    private Set<Question> questions = new HashSet<>();

    @ManyToMany
    private Set<SchoolClass> schoolClasses = new HashSet<>();


    public void addQuestion(Question q){
        questions.add(q);
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Set<SchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    public void setSchoolClasses(Set<SchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    public void addSchoolClass(SchoolClass schoolClass){
        this.schoolClasses.add(schoolClass);
    }
}
