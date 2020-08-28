package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.quizresponse.AnswerFeedbackDTO;
import com.progmatic.progmappbe.dtos.quizresponse.AnswerResponseDTO;
import com.progmatic.progmappbe.services.EternalQuizService;
import org.springframework.web.bind.annotation.*;

@RestController
public class EternalQuizController {

    private EternalQuizService eternalQuizService;

    public EternalQuizController(EternalQuizService eternalQuizService) {
        this.eternalQuizService = eternalQuizService;
    }

    @PostMapping("/eternalquiz")
    public EntityCreationResult createEternalQuiz(@RequestBody EternalQuizDTO edto){
        return eternalQuizService.createEternalQuiz(edto);
    }

    @PutMapping("/eternalquiz/quiz/question")
    public BasicResult assignQuestionToEternalQuiz(
            @RequestBody EternalQuizToQuestionDTO eqqDTO){
        return eternalQuizService.assignQuestionToEternalQuiz(eqqDTO.getEternalQuizId(), eqqDTO.getQuestionId());
    }

    @PutMapping("/eternalquiz/quiz/class")
    public BasicResult assignSchoolClassToEternalQuiz(
            @RequestBody EternalQuizToClassDTO eqcDTO){
        return eternalQuizService.assignQuestionToEternalSchoolClass(eqcDTO.getEternalQuizId(), eqcDTO.getSchoolClassId());
    }

    @GetMapping(path = "/eternalquiz/question")
    public QuestionDTO getNextEternalQiuzQuestion(){
        return eternalQuizService.getNextEternalQuizQuestion();
    }

    @PostMapping(path = "/eternalquiz/answer")
    public AnswerFeedbackDTO acceptEternalQuizAnswer(@RequestBody AnswerResponseDTO answer){
        return eternalQuizService.acceptEternalQuizAnswer(answer);
    }

    @GetMapping(path = "/eternalquiz/me/statistics")
    public EternalQuizStatisticDTO getMyEternalQuizStatistics(){
        return eternalQuizService.getMyEternalQuizStatistics();
    }
}
