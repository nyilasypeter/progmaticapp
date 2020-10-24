/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.progmatic.progmappbe.helpers.MarkdownHelper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author peti
 */
@RestController
public class CommonController {
    
    private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);
    
    @GetMapping(path = "/csrf")
    public String getCsrf(HttpServletRequest request, HttpSession session){
        HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
        CsrfToken csrf = repo.loadToken(request);
        return csrf.getToken();
    }

    @GetMapping(path = "/md2html", produces = {MediaType.TEXT_HTML_VALUE})
    public String markDownToHtml(@RequestParam("text2Convert") String text2Convert){
        return MarkdownHelper.markDownToHTMLSafe(text2Convert);
    }
}
