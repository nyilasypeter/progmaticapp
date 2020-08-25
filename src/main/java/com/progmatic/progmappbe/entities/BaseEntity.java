/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import com.progmatic.progmappbe.helpers.SecHelper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author peti
 */
@MappedSuperclass
public class BaseEntity implements Serializable{ 
   
    @Id
    @Column(length = 40)
    private String id;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String lastModifiedBy;
    
    @PrePersist
    public void insert(){
        if(this.id == null){
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        User loggedInUser = SecHelper.getLoggedInUser();
        if(loggedInUser != null){
            this.createdBy = loggedInUser.getLoginName();
        }
        this.lastModifiedBy = null;
    }
    
    @PreUpdate
    public void update(){
        updatedAt = LocalDateTime.now();
        User loggedInUser = SecHelper.getLoggedInUser();
        if(loggedInUser != null){
            this.lastModifiedBy = loggedInUser.getLoginName();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

}
