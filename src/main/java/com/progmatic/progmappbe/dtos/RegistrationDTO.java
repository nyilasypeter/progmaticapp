package com.progmatic.progmappbe.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.progmatic.progmappbe.helpers.DateHelper;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class RegistrationDTO {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8)
    private String password;

    @Past
    @JsonFormat(pattern = DateHelper.DATE_FORMAT)
    private LocalDate birthDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
