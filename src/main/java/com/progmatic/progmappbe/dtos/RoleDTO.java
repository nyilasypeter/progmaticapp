/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author peti
 */
public class RoleDTO extends BaseEntityDTO{

    public RoleDTO() {
    }

    public RoleDTO(String name) {
        this.name = name;
    }
        
    private String name;
    
    private List<PriviligeDTO> priviliges = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PriviligeDTO> getPriviliges() {
        return priviliges;
    }

    public void setPriviliges(List<PriviligeDTO> priviliges) {
        this.priviliges = priviliges;
    }
    
    public void addPrivilige(PriviligeDTO p){
        this.priviliges.add(p);
    }
    
    
}
