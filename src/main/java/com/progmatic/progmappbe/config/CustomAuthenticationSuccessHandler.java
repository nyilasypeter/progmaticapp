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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.dtos.user.UserDTO;
import com.progmatic.progmappbe.entities.Role;
import com.progmatic.progmappbe.entities.User;
import com.progmatic.progmappbe.helpers.SecHelper;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author peti
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    ObjectMapper jacksonMapper;

    @Autowired
    DozerBeanMapper dozerMapper;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String parameter = request.getParameter(WebSecurityConfig.LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND);
        if(WebSecurityConfig.LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND.equals(parameter) && SecHelper.hasRole(Role.ROLE_ADMIN)){
            response.sendRedirect( "/adminpage");
        }
        else {
            User loggedInUser = SecHelper.getLoggedInUser();
            UserDTO userDTO = dozerMapper.map(loggedInUser, UserDTO.class);
            userDTO.setPassword(null);
            String userJson = jacksonMapper.writeValueAsString(userDTO);
            response.setHeader("Content-Type", "application/json");
            if(StringUtils.isNotBlank(response.getHeader("Set-Cookie"))){
                LOGGER.trace("SameSite=None added to cookie");
                response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=None");
            }
            response.getWriter().write(userJson);
        }
    }
    

}
