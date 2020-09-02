package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.RegistrationDTO;
import com.progmatic.progmappbe.dtos.UserDTO;
import com.progmatic.progmappbe.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegistrationController {

    private RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/completeregistration")
    public BasicResult completeRegistration(@Valid @RequestBody RegistrationDTO registrationDTO){
        return registrationService.completeRegistration(registrationDTO);
    }

    @PutMapping("/me")
    public BasicResult changeMyData(@RequestBody UserDTO userDTO){
        return registrationService.changeMyData(userDTO);
    }
}
