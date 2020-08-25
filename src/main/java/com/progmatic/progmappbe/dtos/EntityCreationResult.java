package com.progmatic.progmappbe.dtos;

import org.apache.commons.lang3.StringUtils;

public class EntityCreationResult extends BasicResult {

    private String idOfCreatedEntity;

    public EntityCreationResult(boolean successfullCreation, String idOfCreatedEntity, String errorMessage) {
        super(successfullCreation, errorMessage);
        this.idOfCreatedEntity = idOfCreatedEntity;
    }

    public String getIdOfCreatedEntity() {
        return idOfCreatedEntity;
    }

    public void setIdOfCreatedEntity(String idOfCreatedEntity) {
        this.idOfCreatedEntity = idOfCreatedEntity;
    }

}
