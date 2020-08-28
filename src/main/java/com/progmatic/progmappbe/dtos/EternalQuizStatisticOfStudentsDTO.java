package com.progmatic.progmappbe.dtos;

import java.util.ArrayList;
import java.util.List;

public class EternalQuizStatisticOfStudentsDTO extends BasicResult {

    public EternalQuizStatisticOfStudentsDTO(){
        super(true, null);
    }

    public EternalQuizStatisticOfStudentsDTO(boolean successFullResult, String errorMessage) {
        super(successFullResult, errorMessage);
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
