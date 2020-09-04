package com.progmatic.progmappbe.dtos.eternalquiz;

import com.progmatic.progmappbe.dtos.BaseEntityDTO;
import com.progmatic.progmappbe.dtos.schoolclass.SchoolClassDTO;

import java.util.List;

public class EternalQuizSearchResponseDTO extends BaseEntityDTO {
    private List<SchoolClassDTO> schoolClasses;

    public List<SchoolClassDTO> getSchoolClasses() {
        return schoolClasses;
    }

    public void setSchoolClasses(List<SchoolClassDTO> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }
}
