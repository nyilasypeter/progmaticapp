package com.progmatic.progmappbe.anwerevaluator;

import com.progmatic.progmappbe.entities.ActualAnswer;
import com.progmatic.progmappbe.entities.ActualAnswerValue;
import com.progmatic.progmappbe.entities.PossibleAnswer;
import com.progmatic.progmappbe.entities.PossibleAnswerValue;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("defaultEvaluator")
public class DefaultEvaluator implements AnswerEvaluator {
    @Override
    @Transactional
    public AnswerEvaulationResult evaluateAnswer(ActualAnswer actAnswer) {
        Map<String, PossibleAnswer> mapOfOrigPossibleAnswers = new HashMap<>();
        Map<String, Set<ActualAnswerValue>> mapByPossibleAnswer = new HashMap<>();
        for (PossibleAnswer possibleAnswer : actAnswer.getQuestion().getPossibleAnswers()) {
            mapByPossibleAnswer.put(possibleAnswer.getId(), new HashSet<>());
            mapOfOrigPossibleAnswers.put(possibleAnswer.getId(), possibleAnswer);
        }
        for (ActualAnswerValue selectedAnswerValue : actAnswer.getSelectedAnswerValues()) {
            PossibleAnswer possibleAnswer = selectedAnswerValue.getPossibleAnswerValue().getPossibleAnswer();
            mapByPossibleAnswer.get(possibleAnswer.getId()).add(selectedAnswerValue);

        }
        AnswerEvaulationResult res = mapOfOrigPossibleAnswers.isEmpty() ? AnswerEvaulationResult.falseAnswer : AnswerEvaulationResult.rightAnswer;
        for (PossibleAnswer possibleAnswer : mapOfOrigPossibleAnswers.values()) {
            Set<ActualAnswerValue> possibleAnswerValues = mapByPossibleAnswer.get(possibleAnswer.getId());
            AnswerEvaulationResult actResult = evaluatePossibleAnswer(possibleAnswer, possibleAnswerValues);
            res = res.and(actResult);
        }
        return res;
    }

    private AnswerEvaulationResult evaluatePossibleAnswer(PossibleAnswer origPossibleAnswer, Set<ActualAnswerValue> selectedAnswerValues){
        switch (origPossibleAnswer.getType()){
            case soruceCodeToOrder_EvalByCompare:
                return evaluateSourceCodeSimple(origPossibleAnswer, selectedAnswerValues);
            case radioButtons:
            case dropdown:
            case checkboxList:
            case trueFalseCheckbox:
                return evaluateAnswerSelectPossibleAnswer(origPossibleAnswer, selectedAnswerValues);
            default:
                throw new RuntimeException("evaluation algorithm not implemented for " + origPossibleAnswer.getType());
        }

    }

    private AnswerEvaulationResult evaluateSourceCodeSimple(PossibleAnswer origPossibleAnswer, Set<ActualAnswerValue> selectedAnswerVals){
       Set<PossibleAnswerValue> origPossibleAnswerValues = origPossibleAnswer.getPossibleAnswerValues();
        if(selectedAnswerVals.size() != origPossibleAnswerValues.size()){
            return AnswerEvaulationResult.falseAnswer;
        }
        List<ActualAnswerValue> selectedAnswerValues = new ArrayList<>(selectedAnswerVals);
        List<PossibleAnswerValue> rightAnswerValues = new ArrayList<>(origPossibleAnswerValues);
        Collections.sort(selectedAnswerValues, Comparator.comparing(ActualAnswerValue::getSelectedOrder));
        Collections.sort(rightAnswerValues, Comparator.comparing(PossibleAnswerValue::getRightOrder));
        for (int i = 0; i < selectedAnswerValues.size(); i++) {
            ActualAnswerValue selectedAnswer = selectedAnswerValues.get(i);
            PossibleAnswerValue rightAnswerValue = rightAnswerValues.get(i);
            if(!selectedAnswer.getPossibleAnswerValue().getText().equals(rightAnswerValue.getText())){
                return AnswerEvaulationResult.falseAnswer;
            }
        }
        return AnswerEvaulationResult.rightAnswer;
    }

    private AnswerEvaulationResult evaluateAnswerSelectPossibleAnswer(PossibleAnswer origPossibleAnswer, Set<ActualAnswerValue> selectedAnswerValues){
          /* no answer is only good if it was a boolean checkbox,
         where the right answer was false. */
        if(selectedAnswerValues.isEmpty()){
            if(origPossibleAnswer.getType().equals(PossibleAnswerType.trueFalseCheckbox)){
                if(!origPossibleAnswer.getPossibleAnswerValues().stream().findFirst().get().getIsRightAnswer()){
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
        for (ActualAnswerValue selectedAnswerValue : selectedAnswerValues) {
            if(!selectedAnswerValue.getPossibleAnswerValue().getIsRightAnswer()){
                return AnswerEvaulationResult.falseAnswer;
            }
        }
        /* all selected answers are right, so the only question is:
        is there any right answer that was not selected? */
        for (PossibleAnswerValue possibleAnswerValue : origPossibleAnswer.getPossibleAnswerValues()) {
            if(possibleAnswerValue.getIsRightAnswer() &&
                    selectedAnswerValues.stream().filter(av -> av.getPossibleAnswerValue().getId().equals(possibleAnswerValue.getId())).count() == 0){
                return AnswerEvaulationResult.partiallyRightAnswer;
            }
        }
        return AnswerEvaulationResult.rightAnswer;
    }
}
