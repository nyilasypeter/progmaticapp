/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import com.progmatic.progmappbe.aoutodaos.UserAutoDao;
import com.progmatic.progmappbe.entities.User;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author peti
 */
public class ProgmaUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgmaUserDetailsService.class);

    @Autowired
    UserAutoDao userAutoDao;

    @PersistenceContext
    EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("gonna load user: {}", username);
        try {

            EntityGraph<?> eg = em.createEntityGraph("userWithRolesAndPriviliges");

            Map<String, Object> map = new HashMap<>();
            map.put("javax.persistence.fetchgraph", eg);
            User user = em.createQuery("select u from User u join fetch u.roles r join fetch r.priviliges where u.id = :loginName", User.class)
                    .setParameter("loginName", username)
                    .getSingleResult();
            //User user = em.find(User.class, username, map);
            LOGGER.debug("user found");
            return user;
        } catch (NoResultException ex) {
            LOGGER.debug("user not found");
            throw new UsernameNotFoundException(username);
        }

    }

}
