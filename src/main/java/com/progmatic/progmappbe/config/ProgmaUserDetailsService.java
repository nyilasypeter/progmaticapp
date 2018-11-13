/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import com.progmatic.progmappbe.aoutodaos.UserAutoDao;
import com.progmatic.progmappbe.entities.User;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
            Query q = em.createQuery("select u from User u where u.loginName = :userName");
            q.setParameter("userName", username);
            EntityGraph<?> eg = em.createEntityGraph("userWithRolesAndPriviliges");
            q.setHint("javax.persistence.fetchgraph", eg);
            User user = (User) q.getSingleResult();
            LOGGER.debug("user found");
            return user;
        } catch (NoResultException ex) {
            LOGGER.debug("user not found");
            throw new UsernameNotFoundException(username);
        }

    }

}
