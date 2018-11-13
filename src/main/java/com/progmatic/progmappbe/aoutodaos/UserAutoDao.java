/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.aoutodaos;

import com.progmatic.progmappbe.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author peti
 */
public interface UserAutoDao extends JpaRepository<User, String>{
    public User findByLoginName(String loginName);
}
