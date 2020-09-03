package com.progmatic.progmappbe.dtos;

import java.util.ArrayList;
import java.util.List;

public class BasicResult {

    private boolean successFullResult;
    private List<ErrorMsg> errorMessages = new ArrayList<>();
    private List<String> notes = new ArrayList<>();


    public boolean isSuccessFullResult() {
        return successFullResult;
    }

    public void setSuccessFullResult(boolean successFullResult) {
        this.successFullResult = successFullResult;
    }

    public List<ErrorMsg> getErrorMessages() {
        return errorMessages;
    }

    public void addErrorMessage(ErrorMsg msg){
        errorMessages.add(msg);
    }

    public void addErrorMessage(String code, String value){
        errorMessages.add(new ErrorMsg(code, value));
    }

    public void setErrorMessages(List<ErrorMsg> errorMessages) {
        this.errorMessages = errorMessages;
    }



    public void addNote(String note){
        notes.add(note);
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
