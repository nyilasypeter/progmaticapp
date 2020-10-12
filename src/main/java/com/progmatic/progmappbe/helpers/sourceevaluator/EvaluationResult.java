package com.progmatic.progmappbe.helpers.sourceevaluator;

import java.util.ArrayList;
import java.util.List;

public class EvaluationResult {

    private Boolean unitTestSuccessfull = false;
    private Boolean compilationSuccessfull = false;
    private List<String> errorMessages = new ArrayList<String>();

    public Boolean isSuccessfull(){
        return  unitTestSuccessfull && compilationSuccessfull;
    }

    public Boolean getUnitTestSuccessfull() {
        return unitTestSuccessfull;
    }

    public void setUnitTestSuccessfull(Boolean unitTestSuccessfull) {
        this.unitTestSuccessfull = unitTestSuccessfull;
    }

    public Boolean getCompilationSuccessfull() {
        return compilationSuccessfull;
    }

    public void setCompilationSuccessfull(Boolean compilationSuccessfull) {
        this.compilationSuccessfull = compilationSuccessfull;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
                "unitTestSuccessfull=" + unitTestSuccessfull +
                ", compilationSuccessfull=" + compilationSuccessfull +
                ", errorMessages=" + errorMessages +
                '}';
    }
}
