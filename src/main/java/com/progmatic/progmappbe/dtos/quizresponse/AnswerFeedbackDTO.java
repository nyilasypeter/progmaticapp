package com.progmatic.progmappbe.dtos.quizresponse;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.PossibleAnswerDTO;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;

import java.util.ArrayList;
import java.util.List;

public class AnswerFeedbackDTO extends BasicResult {

    private AnswerEvaulationResult result;
    private String feedback;
    private List<PossibleAnswerDTO> rigthAnswers = new ArrayList<>();

    public AnswerFeedbackDTO() {
    }

    public AnswerFeedbackDTO(BasicResult br){
        this.setSuccessFullResult(br.isSuccessFullResult());
        this.setErrorMessages(br.getErrorMessages());
        this.setNotes(br.getNotes());

    }

    public void addRgithAnswer(PossibleAnswerDTO possibleAnswerDTO){
        rigthAnswers.add(possibleAnswerDTO);
    }

    public AnswerEvaulationResult getResult() {
        return result;
    }

    public void setResult(AnswerEvaulationResult result) {
        this.result = result;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<PossibleAnswerDTO> getRigthAnswers() {
        return rigthAnswers;
    }

    public void setRigthAnswers(List<PossibleAnswerDTO> rigthAnswers) {
        this.rigthAnswers = rigthAnswers;
    }
}
