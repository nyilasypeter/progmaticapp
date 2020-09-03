package com.progmatic.progmappbe.dtos;

public class ErrorMsg {
    private String code;
    private String localizedMessage;

    public ErrorMsg(){

    }

    public ErrorMsg(String code, String localizedMessage) {
        this.code = code;
        this.localizedMessage = localizedMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }
}
