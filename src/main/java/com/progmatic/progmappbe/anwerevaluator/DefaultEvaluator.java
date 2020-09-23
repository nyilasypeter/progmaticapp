package com.progmatic.progmappbe.anwerevaluator;

import com.progmatic.progmappbe.entities.ActualAnswer;
import com.progmatic.progmappbe.entities.PossibleAnswer;
import com.progmatic.progmappbe.entities.PossibleAnswerValue;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service("defaultEvaluator")
public class DefaultEvaluator implements AnswerEvaluator {
    @Override
    @Transactional
    public AnswerEvaulationResult evaluateAnswer(ActualAnswer actAnswer) {
        Map<String, PossibleAnswer> mapOfPossibleAnswers = new HashMap<>();
        Map<String, Set<PossibleAnswerValue>> mapByPossibleAnswer = new HashMap<>();
        for (PossibleAnswer possibleAnswer : actAnswer.getQuestion().getPossibleAnswers()) {
            mapByPossibleAnswer.put(possibleAnswer.getId(), new HashSet<>());
            mapOfPossibleAnswers.put(possibleAnswer.getId(), possibleAnswer);
        }
        for (PossibleAnswerValue selectedAnswerValue : actAnswer.getSelectedAnswerValues()) {
            PossibleAnswer possibleAnswer = selectedAnswerValue.getPossibleAnswer();
            mapByPossibleAnswer.get(possibleAnswer.getId()).add(selectedAnswerValue);

        }
        AnswerEvaulationResult res = mapOfPossibleAnswers.isEmpty() ? AnswerEvaulationResult.falseAnswer : AnswerEvaulationResult.rightAnswer;
        for (PossibleAnswer possibleAnswer : mapOfPossibleAnswers.values()) {
            Set<PossibleAnswerValue> possibleAnswerValues = mapByPossibleAnswer.get(possibleAnswer.getId());
            AnswerEvaulationResult actResult = evaluatePossibleAnswer(possibleAnswer, possibleAnswerValues);
            res = res.and(actResult);
        }
        return res;
    }

    private AnswerEvaulationResult evaluatePossibleAnswer(PossibleAnswer possibleAnswer, Set<PossibleAnswerValue> selectedAnswerValues){
        /* no answer is only good if it was a boolean checkbox,
         where the right answer was false. */
        if(selectedAnswerValues.isEmpty()){
            if(possibleAnswer.getType().equals(PossibleAnswerType.trueFalseCheckbox)){
                if(!possibleAnswer.getPossibleAnswerValues().stream().findFirst().get().getIsRightAnswer()){
                    return AnswerEvaulationResult.rightAnswer;
                }
                else{
                    return AnswerEvaulationResult.falseAnswer;
                }
            }
            else{
                return AnswerEvaulationResult.falseAnswer;
            }
        }
        /* if any false answer is selected, it is a false answer */
        for (PossibleAnswerValue selectedAnswerValue : selectedAnswerValues) {
            if(!selectedAnswerValue.getIsRightAnswer()){
                return AnswerEvaulationResult.falseAnswer;
            }
        }
        /* all selected answers are right, so the only question is:
        is there any right answer that was not selected? */
        for (PossibleAnswerValue possibleAnswerValue : possibleAnswer.getPossibleAnswerValues()) {
            if(possibleAnswerValue.getIsRightAnswer() &&
                    selectedAnswerValues.stream().filter(av -> av.getId().equals(possibleAnswerValue.getId())).count() == 0){
                return AnswerEvaulationResult.partiallyRightAnswer;
            }
        }
        return AnswerEvaulationResult.rightAnswer;
    }
}
