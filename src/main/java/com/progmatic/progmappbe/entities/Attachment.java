package com.progmatic.progmappbe.entities;

import javax.persistence.*;

/**
 * This class contains every attachments (pictures, pdfs, documents, etc) stored in the database.
 * Attacments are related to other entities.
 * If the relation is one-to-one, then entityId and the id of the Attachment will be same, and it will be
 * equal to the entity's id.
 * If the relation is many-to-one (many files to one entity), then the entityId will refer to the entity, while
 * each attachments will have a different id.
 *
 * See also AttachmentService.java
 */
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "attachmentWithoutFile",
                includeAllAttributes = false,
                attributeNodes = {
                        @NamedAttributeNode(value = "entityId"),
                        @NamedAttributeNode(value = "imageContentType"),
                        @NamedAttributeNode(value = "createdAt"),
                        @NamedAttributeNode(value = "createdBy"),
                        @NamedAttributeNode(value = "imageContentType"),

                        @NamedAttributeNode(value = "id")  }

        )
})
public class Attachment extends BaseEntity{

    private String entityId;

    @Lob
    @Column(length=2_000_000)
    private byte[] image;

    private String imageContentType;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }
}
