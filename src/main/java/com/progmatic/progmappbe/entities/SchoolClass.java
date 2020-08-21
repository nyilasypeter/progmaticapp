package com.progmatic.progmappbe.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SchoolClass extends BaseEntity{
    public static final String SEMESTER_AUTUMN = "SEMESTER_AUTUMN";
    public static final String SEMESTER_SPRING = "SEMESTER_SPRING";

    private Integer year;
    private SemesterType semester;
    private Boolean isActive;

    @ManyToMany
    private Set<User> students = new HashSet<>();

    public void addStudent(User u){
        students.add(u);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public SemesterType getSemester() {
        return semester;
    }

    public void setSemester(SemesterType semester) {
        this.semester = semester;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
    }
}
