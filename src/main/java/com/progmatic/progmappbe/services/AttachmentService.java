package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.AttachmentDTO;
import com.progmatic.progmappbe.dtos.BaseEntityDTO;
import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.entities.Attachment;
import com.progmatic.progmappbe.entities.BaseEntity;
import com.progmatic.progmappbe.entities.Question;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentService {

    @PersistenceContext
    private EntityManager em;


    private DozerBeanMapper mapper;

    @Autowired
    public AttachmentService(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    public BasicResult uploadOneFileToOneEntity(String entityId, MultipartFile file) {
        Attachment attachment = em.find(Attachment.class, entityId);
        if(attachment == null){
            attachment = new Attachment();
            attachment.setEntityId(entityId);
            attachment.setId(entityId);
            em.persist(attachment);
        }
        try {
            attachment.setImage(file.getBytes());
            attachment.setImageContentType(file.getContentType());
            return new BasicResult(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public BasicResult uploadManyFileToOneEntity(String entityId, MultipartFile file) {
        try {
            Attachment attachment = new Attachment();
            attachment.setEntityId(entityId);
            em.persist(attachment);
            attachment.setImage(file.getBytes());
            attachment.setImageContentType(file.getContentType());
            return new BasicResult(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public BasicResult removeAttachment(String attachmentId){
        Attachment attachment = em.find(Attachment.class, attachmentId);
        if(attachment == null){
            em.remove(attachment);
            return new BasicResult(true);
        }
        else{
            return new BasicResult(false, "No attachment with this id");
        }

    }

    /***
     * Loads an attachment based on it's id.
     * @param attachmentId is the attachment entity's id.
     *                     It is also equal to the related entity id,
     *                     if the relationship between the entity and the attachment is one ot one.
     * @return
     */
    public ResponseEntity<Resource> loadOneToOneFile(String attachmentId){
        Attachment attachment = em.find(Attachment.class, attachmentId);
        if(attachment == null || attachment.getImage() == null){
            return null;
        }
        ByteArrayResource resource = new ByteArrayResource(attachment.getImage());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getImageContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;")
                .body(resource);
    }

    public List<AttachmentDTO> listAllFilesToEntity(String entityId){
        EntityGraph graph = this.em.getEntityGraph("attachmentWithoutFile");
        List<Attachment> attachments = em.createQuery("select a from Attachment a where a.entityId = :id", Attachment.class)
                .setParameter("id", entityId)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
        List<AttachmentDTO> ret = new ArrayList<>();
        for (Attachment attachment : attachments) {
            ret.add(mapper.map(attachment, AttachmentDTO.class));
        }
        return ret;
    }
}