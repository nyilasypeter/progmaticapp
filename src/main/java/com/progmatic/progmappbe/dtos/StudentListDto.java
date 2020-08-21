package com.progmatic.progmappbe.dtos;

import java.util.HashSet;
import java.util.Set;

public class StudentListDto {

    private Set<String> idList = new HashSet<>();

    public Set<String> getIdList() {
        return idList;
    }

    public void setIdList(Set<String> idList) {
        this.idList = idList;
    }
}
