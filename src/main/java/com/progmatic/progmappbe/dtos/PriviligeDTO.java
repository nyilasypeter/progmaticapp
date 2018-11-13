/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos;

/**
 *
 * @author peti
 */
public class PriviligeDTO extends BaseEntityDTO{

    public PriviligeDTO() {
    }

    public PriviligeDTO(String name) {
        this.name = name;
    }
    
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
