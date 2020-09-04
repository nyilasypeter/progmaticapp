package com.progmatic.progmappbe.anwerevaluator;

import com.progmatic.progmappbe.entities.ActualAnswer;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;

import java.util.Map;

public interface AnswerEvaluator {

    public static final Map<String, String> EVAL_ALG_MAP = Map.ofEntries(
            //Map.entry("ize", "bigyo")
    );


    AnswerEvaulationResult evaluateAnswer(ActualAnswer response);
}
