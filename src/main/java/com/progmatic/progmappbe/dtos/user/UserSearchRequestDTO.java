package com.progmatic.progmappbe.dtos.user;

public class UserSearchRequestDTO {
    private String loginName;
    private String name;
    private Boolean isStudent;
    private Boolean hasUnfinishedRegistration;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isStudent() {
        return isStudent;
    }

    public void setStudent(Boolean student) {
        isStudent = student;
    }

    public Boolean isHasUnfinishedRegistration() {
        return hasUnfinishedRegistration;
    }

    public void setHasUnfinishedRegistration(Boolean hasUnfinishedRegistration) {
        this.hasUnfinishedRegistration = hasUnfinishedRegistration;
    }
}
