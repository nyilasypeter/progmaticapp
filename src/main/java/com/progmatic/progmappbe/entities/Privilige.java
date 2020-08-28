/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 *
 * @author peti
 */
@Entity
public class Privilige extends BaseEntity{
    
    public static final String PRIV_CREATE_QUESTION = "createQuestion";
    public static final String PRIV_READ_QUESTION = "readQuestion";
    public static final String PRIV_CREATE_TEST = "createTest";
    public static final String PRIV_START_TEST = "startTest";
    public static final String PRIV_CREATE_CLASS = "createClass" ;
    public static final String PRIV_CREATE_STUDENT = "createStudent" ;
    public static final String PRIV_CREATE_USER = "createUser" ;
    public static final String PRIV_CRUD_ETERNAL_QUIZ = "crudEternalQuiz" ;
    public static final String PRIV_OWN_ETERNAL_QUIZ_STATISTICS = "privOwnEternalQuizStatistics" ;
    public static final String PRIV_ETERNAL_QUIZ_STATISTICS_OF_ANY_STUDENT = "privEternalQuizStatisticsOfAnyStudent" ;

    public Privilige() {
    }

    public Privilige(String name) {
        this.name = name;
    }
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @ManyToMany
    private Set<Role> roles = new  HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    public void addRole(Role r){
        this.roles.add(r);
    }
    
    
}
