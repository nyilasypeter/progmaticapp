package com.progmatic.progmappbe.dtos.template;

import com.progmatic.progmappbe.dtos.BaseEntityRequestDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MailTemplateDTO extends BaseEntityRequestDto {

    @NotNull
    @NotEmpty
    private String body;

    private String subject;

    private Boolean isHtml;

    private String cc;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getHtml() {
        return isHtml;
    }

    public void setHtml(Boolean html) {
        isHtml = html;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }
}
