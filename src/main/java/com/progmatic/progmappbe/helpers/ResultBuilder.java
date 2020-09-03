package com.progmatic.progmappbe.helpers;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.entities.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ResultBuilder {

    private MessageSource messageSource;

    @Autowired
    public ResultBuilder(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public  BasicResult errorResult(String errKey, String... params){
        String message = messageSource.getMessage(errKey, params, LocaleContextHolder.getLocale());
        BasicResult ret = new  BasicResult();
        ret.setSuccessFullResult(false);
        ret.addErrorMessage(errKey, message);
        return ret;
    }

    public BasicResult okResult(){
        BasicResult ret = new  BasicResult();
        ret.setSuccessFullResult(true);
        return ret;
    }

    public BasicResult okResult(String note){
        BasicResult ret = new  BasicResult();
        ret.setSuccessFullResult(true);
        if(StringUtils.isNotBlank(note)){
            ret.addNote(note);
        }
        return ret;
    }

    public EntityCreationResult errorEntityCreateResult(String errKey, String... params){
        String message = messageSource.getMessage(errKey, params, LocaleContextHolder.getLocale());
        EntityCreationResult ret = new EntityCreationResult();
        ret.setSuccessFullResult(false);
        ret.addErrorMessage(errKey, message);
        return ret;
    }

    public EntityCreationResult okEntityCreateResult(String id){
        EntityCreationResult ret = new EntityCreationResult();
        ret.setSuccessFullResult(true);
        ret.setIdOfCreatedEntity(id);
        return ret;
    }

    public EntityCreationResult okEntityCreateResult(String id, String note){
        EntityCreationResult ret = new EntityCreationResult();
        ret.setSuccessFullResult(true);
        if(StringUtils.isNotBlank(note)) {
            ret.addNote(note);
        }
        return ret;
    }

    public EntityCreationResult okEntityCreateResult(BaseEntity baseEntity){
        return this.okEntityCreateResult(baseEntity.getId());
    }

    public EntityCreationResult okEntityCreateResult(BaseEntity baseEntity, String notes){
        return this.okEntityCreateResult(baseEntity.getId(), notes);
    }

    public String translate(String key){
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }


    public String translate(String key, String... args){
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
