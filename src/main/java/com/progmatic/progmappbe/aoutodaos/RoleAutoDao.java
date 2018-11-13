/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.aoutodaos;

import com.progmatic.progmappbe.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author peti
 */
public interface RoleAutoDao extends JpaRepository<Role, String>{
    public Role findByName(String name);
}
