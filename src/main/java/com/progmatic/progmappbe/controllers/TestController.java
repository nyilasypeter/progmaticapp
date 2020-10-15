/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.quiz.OrderLinesQuestionRequestDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionSearchDto;
import com.progmatic.progmappbe.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 * @author peti
 */
@RestController
public class TestController {
    
    private TestService testService;



    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping(path = "/question")
    public EntityCreationResult createQuestion(@RequestBody QuestionDTO q){
        return testService.createQuestion(q);
    }

    @PostMapping(path = "/orderlinesquestion")
    public EntityCreationResult createOrderLinesQuestion(@RequestBody OrderLinesQuestionRequestDTO q){
        return testService.createOrderLinesQuestion(q);
    }

    @PutMapping(path = "/question")
    public BasicResult updateQuestion(@RequestBody QuestionDTO q){
        return testService.updateQuestion(q);
    }
    
    @GetMapping(path = "question/{questionId}")
    public QuestionDTO findQuestion(@PathVariable("questionId") String questionId){
        return testService.findQuestion(questionId);
    }

    @PostMapping(path = "/question/search")
    public List<QuestionDTO> findQuestions(@RequestBody QuestionSearchDto searchDto){
        return testService.findQuestions(searchDto);
    }
    
    @PostMapping(path = "/questioncopy/{questionId}")
    public String copyQuestion(@PathVariable("questionId") String questionId){
        return testService.copyQuestion(questionId);
    }

    @PostMapping(path = "/question/{questionId}/imagefile")
    public BasicResult uplaoFileToQuestion(
            @PathVariable("questionId") String questionId,
            @RequestParam("file") MultipartFile file){
        return testService.uplaoFileToQuestion(questionId, file);
    }

    @GetMapping(path = "/question/{questionId}/imagefile")
    public ResponseEntity<Resource> loadImageOfQuestion(@PathVariable("questionId")  String questionId){
       return  testService.loadImageOfQuestion(questionId);

    }

    @PostMapping(path = "/question/possibleanswer/{possibleAnswerId}/imagefile")
    public BasicResult uplaoFileToPossibleAnswer(
            @PathVariable("possibleAnswerId") String possibleAnswerId,
            @RequestParam("file") MultipartFile file){
        return testService.uplaoFileToPossibleAnswer(possibleAnswerId, file);
    }

    @GetMapping(path = "/question/possibleanswer/{possibleAnswerId}/imagefile")
    public ResponseEntity<Resource> loadImageOfPossibleAnswer(@PathVariable("possibleAnswerId")  String possibleAnswerId){
        return  testService.loadImageOfPossibleAnswer(possibleAnswerId);

    }
    
}
