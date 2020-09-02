package com.progmatic.progmappbe.dtos;


import java.util.ArrayList;
import java.util.List;

public class SearchUserResponseDTO extends BaseEntityDTO {
    private String name;

    private String loginName;

    private String emailAddress;

    private boolean hasUnfinishedRegistration = false;

    private List<RoleDTO> roles = new ArrayList<>();

    private List<SchoolClassDTO> classes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isHasUnfinishedRegistration() {
        return hasUnfinishedRegistration;
    }

    public void setHasUnfinishedRegistration(boolean hasUnfinishedRegistration) {
        this.hasUnfinishedRegistration = hasUnfinishedRegistration;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public List<SchoolClassDTO> getClasses() {
        return classes;
    }

    public void setClasses(List<SchoolClassDTO> classes) {
        this.classes = classes;
    }
}
