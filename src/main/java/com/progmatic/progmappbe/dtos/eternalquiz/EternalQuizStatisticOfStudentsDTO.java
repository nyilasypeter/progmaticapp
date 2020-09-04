package com.progmatic.progmappbe.dtos.eternalquiz;

import com.progmatic.progmappbe.dtos.BasicResult;

import java.util.ArrayList;
import java.util.List;

public class EternalQuizStatisticOfStudentsDTO extends BasicResult {

    public EternalQuizStatisticOfStudentsDTO(){
    }

    public EternalQuizStatisticOfStudentsDTO(BasicResult basicResult) {
        setSuccessFullResult(basicResult.isSuccessFullResult());
        setNotes(basicResult.getNotes());
        setErrorMessages(basicResult.getErrorMessages());
    }

    private List<EternalQuizStatisticDTO> studentStatistics = new ArrayList<>();

    public List<EternalQuizStatisticDTO> getStudentStatistics() {
        return studentStatistics;
    }

    public void addStatistic(EternalQuizStatisticDTO stat){
        studentStatistics.add(stat);
    }

    public void setStudentStatistics(List<EternalQuizStatisticDTO> studentStatistics) {
        this.studentStatistics = studentStatistics;
    }
}
