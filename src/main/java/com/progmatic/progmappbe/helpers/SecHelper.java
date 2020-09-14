/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.helpers;

import com.progmatic.progmappbe.entities.User;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author peti
 */
public class SecHelper {

    public static User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

    public static boolean hasAuthority(String authority) {
        return  hasAuthority((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), authority);
    }

    public static boolean hasAuthority(User u, String authority) {

        Collection<? extends GrantedAuthority> authorities
                = u.getAuthorities();
        for (GrantedAuthority auth : authorities) {
            if (auth.getAuthority().equals(authority)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(String authority) {
        return  hasRole((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), authority);
    }

    public static boolean hasRole(User u, String authority) {

        Collection<? extends GrantedAuthority> authorities
                = u.getAuthorities();
        for (GrantedAuthority auth : authorities) {
            if (auth.getAuthority().equals("ROLE_" + authority)) {
                return true;
            }
        }
        return false;
    }
}
