package com.progmatic.progmappbe.dtos.attachment;


import com.progmatic.progmappbe.dtos.BaseEntityDTO;

public class AttachmentDTO extends BaseEntityDTO {

    private String entityId;

    private String imageContentType;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }
}
