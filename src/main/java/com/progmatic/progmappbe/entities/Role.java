/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

/**
 *
 * @author peti
 */
@Entity
public class Role extends BaseEntity{
    
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_OFFICE = "office";
    public static final String ROLE_STUDENT = "student";

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
        
    @Column(nullable = false, unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "roles")
    private Set<Privilige> priviliges = new HashSet<>();
    
    @ManyToMany
    private Set<User> users = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Privilige> getPriviliges() {
        return priviliges;
    }

    public void setPriviliges(Set<Privilige> priviliges) {
        this.priviliges = priviliges;
    }
    
    public void addPrivilige(Privilige p){
        this.priviliges.add(p);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    public void addUser(User u){
        this.users.add(u);
    }
    
}
