package com.progmatic.progmappbe.helpers;

import com.progmatic.progmappbe.dtos.user.RoleDTO;
import com.progmatic.progmappbe.dtos.user.UserModificationDTO;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserModificationDTOBuilder {

    UserModificationDTO seed = new UserModificationDTO();

    public UserModificationDTOBuilder setName(String name) {
        seed.setName(name);
        return this;
    }


    public UserModificationDTOBuilder setLoginName(String loginName) {
        seed.setLoginName(loginName);
        return this;
    }


    public UserModificationDTOBuilder setEmailAddress(String emailAddress) {
        seed.setEmailAddress(emailAddress);
        return this;
    }



    public UserModificationDTOBuilder setPassword(String password) {
        seed.setPassword(password);
        return this;
    }


    public UserModificationDTOBuilder setBirthDate(LocalDate birthDate) {
        seed.setBirthDate(birthDate);
        return this;
    }


    public UserModificationDTOBuilder setAccountNonLocked(Boolean accountNonLocked) {
        seed.setAccountNonLocked(accountNonLocked);
        return this;
    }


    public UserModificationDTOBuilder addRole(String name) {
        RoleDTO role = new RoleDTO();
        role.setName(name);
        if(seed.getRoles() == null){
            seed.setRoles(new ArrayList<>());
        }
        seed.getRoles().add(role);
        return this;
    }

    public UserModificationDTO build(){
        return seed;
    }

    public static UserModificationDTOBuilder newBuilder(){
        return new UserModificationDTOBuilder();
    }
}
