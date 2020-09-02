/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import com.progmatic.progmappbe.aoutodaos.MailTemplateAutoDao;
import com.progmatic.progmappbe.aoutodaos.PriviligeAutoDao;
import com.progmatic.progmappbe.aoutodaos.RoleAutoDao;
import com.progmatic.progmappbe.aoutodaos.UserAutoDao;
import com.progmatic.progmappbe.entities.MailTemplate;
import com.progmatic.progmappbe.entities.Privilige;
import com.progmatic.progmappbe.entities.Role;
import com.progmatic.progmappbe.entities.User;
import com.progmatic.progmappbe.helpers.MailHelper;
import com.progmatic.progmappbe.services.ConstantService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author peti
 */
@Component
public class ExtraDataLoaderForTests extends DataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ExtraDataLoaderForTests.class);

    public ExtraDataLoaderForTests(UserAutoDao userAutoDao, PasswordEncoder passwordEncoder,
                                   PriviligeAutoDao priviligeAutoDao, RoleAutoDao roleAutoDao,
                                   @Value("${progmatic.admin.default.password}") String adminPassword,
                                   @Value("${progmatic.admin.default.username}") String adminUsername,
                                   ConstantService constantService,
                                   MailTemplateAutoDao mailTemplateAutoDao) {
        super(userAutoDao, passwordEncoder, priviligeAutoDao, roleAutoDao, adminPassword, adminUsername, constantService, mailTemplateAutoDao);

    }


    @Override
    protected void createUsers() {
        super.createUsers();

        Role teacherRole = roleAutoDao.findByName(Role.ROLE_TEACHER);
        Role officeRole = roleAutoDao.findByName(Role.ROLE_OFFICE);
        Role studentRole = roleAutoDao.findByName(Role.ROLE_STUDENT);

        User student = createUser("student");
        studentRole.addUser(student);
        userAutoDao.save(student);
        LOG.debug("student user created.");

        User teacher = createUser("teacher");
        teacherRole.addUser(teacher);
        userAutoDao.save(teacher);
        LOG.debug("teacher user created.");

        User officeUser = createUser("officeUser");
        officeRole.addUser(officeUser);
        userAutoDao.save(officeUser);
        LOG.debug("officeUser user created.");

    }

    private User createUser(String name){
        User user = new User();
        user.setName(name);
        user.setId(name);
        user.setPassword(passwordEncoder.encode(name));
        user.setEmailAddress(name+"@progmatic.hu");
        return user;
    }
}
