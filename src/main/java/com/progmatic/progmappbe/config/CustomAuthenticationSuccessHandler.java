/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import java.io.IOException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.progmatic.progmappbe.entities.Role;
import com.progmatic.progmappbe.helpers.SecHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author peti
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String parameter = request.getParameter(WebSecurityConfig.LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND);
        if(WebSecurityConfig.LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND.equals(parameter) && SecHelper.hasRole(Role.ROLE_ADMIN)){
            response.sendRedirect( "/adminpage");
        }
        else {
            response.getWriter().write("{\"name\":\"" + authentication.getName() + "\"}");
        }
    }
    

}