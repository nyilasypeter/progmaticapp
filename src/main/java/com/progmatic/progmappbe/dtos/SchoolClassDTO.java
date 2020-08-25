package com.progmatic.progmappbe.dtos;


import com.progmatic.progmappbe.entities.enums.SemesterType;


public class SchoolClassDTO extends BaseEntityDTO {

    private Integer year;
    private SemesterType semester;
    private Boolean isActive = true;


    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public SemesterType getSemester() {
        return semester;
    }

    public void setSemester(SemesterType semester) {
        this.semester = semester;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
