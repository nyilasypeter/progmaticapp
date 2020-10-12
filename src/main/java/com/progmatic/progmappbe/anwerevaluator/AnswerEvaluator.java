package com.progmatic.progmappbe.anwerevaluator;

import com.progmatic.progmappbe.entities.ActualAnswer;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;

import java.util.HashMap;
import java.util.Map;

public interface AnswerEvaluator {

    public static final Map<String, String> EVAL_ALG_MAP = new HashMap<>();


    AnswerEvaulationResult evaluateAnswer(ActualAnswer response);
}
