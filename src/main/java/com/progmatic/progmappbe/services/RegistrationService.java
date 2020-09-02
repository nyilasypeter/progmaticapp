package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.RegistrationDTO;
import com.progmatic.progmappbe.dtos.UserDTO;
import com.progmatic.progmappbe.entities.Privilige;
import com.progmatic.progmappbe.entities.User;
import com.progmatic.progmappbe.helpers.SecHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Service
public class RegistrationService {

    @PersistenceContext
    private EntityManager em;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public BasicResult completeRegistration(RegistrationDTO registrationDTO){
        User user;
        try {
            user = em.createQuery("select u from User u where u.registrationToken = :token", User.class)
                    .setParameter("token", registrationDTO.getToken())
                    .getSingleResult();
        }
        catch (NoResultException ex){
            return new BasicResult(false, "Token not found");
        }
        if(user.getRegistrationTokenValidTo().isBefore(LocalDateTime.now())){
            return new BasicResult(false, "Token expired");
        }
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRegistrationToken(null);
        user.setRegistrationTokenValidTo(null);
        user.setEnabled(true);
        user.setBirthDate(registrationDTO.getBirthDate());
        return new BasicResult(true);

    }

    @Transactional
    public BasicResult changeMyData(UserDTO userDTO){
        User loggedInUser = SecHelper.getLoggedInUser();
        loggedInUser = em.merge(loggedInUser);
        if(StringUtils.isNotBlank(userDTO.getPassword())){
            if(passwordEncoder.matches(userDTO.getOldPassword(), loggedInUser.getPassword())){
                loggedInUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            else{
                return new BasicResult(false, "old password did not match");
            }
        }
        loggedInUser.setName(userDTO.getName());
        loggedInUser.setBirthDate(userDTO.getBirthDate());
        return new BasicResult(true);

    }


}
