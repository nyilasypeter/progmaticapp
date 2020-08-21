/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.QuestionDTO;
import com.progmatic.progmappbe.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author peti
 */
@RestController
public class TestController {
    
    @Autowired
    TestService testService;
    
    @PostMapping(path = "/question")
    public String createQuestion(@RequestBody QuestionDTO q){
        return testService.createQuestion(q);
    }
    
    @GetMapping(path = "question/{questionId}")
    public QuestionDTO findQuestion(@PathVariable("questionId") String questionId){
        return testService.findQuestion(questionId);
    }
    
    @PostMapping(path = "/questioncopy/{questionId}")
    public String copyQuestion(@PathVariable("questionId") String questionId){
        return testService.copyQuestion(questionId);
    }
    
}
