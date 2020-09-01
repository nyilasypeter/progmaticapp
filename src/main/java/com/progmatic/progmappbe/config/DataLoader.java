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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.progmatic.progmappbe.helpers.MailHelper;
import com.progmatic.progmappbe.services.ConstantService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author peti
 */
@Component
public class DataLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);



    @PersistenceContext
    EntityManager em;

    private UserAutoDao userAutoDao;

    private PasswordEncoder passwordEncoder;

    private PriviligeAutoDao priviligeAutoDao;

    private RoleAutoDao roleAutoDao;

    private String adminPassword;

    private String adminUsername;

    private ConstantService constantService;

    private MailTemplateAutoDao mailTemplateAutoDao;

    public DataLoader(UserAutoDao userAutoDao, PasswordEncoder passwordEncoder,
                      PriviligeAutoDao priviligeAutoDao, RoleAutoDao roleAutoDao,
                      @Value("${progmatic.admin.default.password}") String adminPassword,
                      @Value("${progmatic.admin.default.username}") String adminUsername,
                      ConstantService constantService,
                      MailTemplateAutoDao mailTemplateAutoDao) {
        this.userAutoDao = userAutoDao;
        this.passwordEncoder = passwordEncoder;
        this.priviligeAutoDao = priviligeAutoDao;
        this.roleAutoDao = roleAutoDao;
        this.adminPassword = adminPassword;
        this.constantService = constantService;
        this.mailTemplateAutoDao = mailTemplateAutoDao;
        this.adminUsername = adminUsername;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createPriviliges();
        createRoles();
        createUsers();
        createConstants();
        createMailTemplates();
    }

    private void createMailTemplates() {
        long mailTemplates = mailTemplateAutoDao.count();
        if(mailTemplates == 0){
            MailTemplate mt = new MailTemplate();
            mt.setId(MailHelper.MAIL_TEMPLATE_STUDENT_REGISTRATION);
            mt.setSubject("progmatic regisztráció");
            mt.setBody("Kedves [(${recipient.name})]!  \n\n Ezen a linken: [(${registrationLink})] tudsz regisztrálni a Progmatic-ba.");
            mt.setHtml(false);
            mailTemplateAutoDao.save(mt);
        }
    }

    private void createConstants() {
        Long constantCount = (Long) em.createQuery("select count(c) from Constant  c").getSingleResult();
        if(constantCount == 0){
            constantService.writeConstant(ConstantService.KEY_ETERNALQUIZ_TARGET_PERCENTAGE, "80");
        }
    }

    private void createPriviliges() {
        long priviliges = priviligeAutoDao.count();
        if(priviliges == 0){
            List<Field> fields = FieldUtils.getAllFieldsList(Privilige.class);
            fields.stream()
                    .filter(field -> Modifier.isStatic(field.getModifiers()))
                    .map(field -> {
                        try {
                            return field.get(null).toString();
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEach(privName -> priviligeAutoDao.save(new Privilige(privName)));
            LOG.debug("Priviliges created.");
        }
    }

    private void createRoles() {
        long roles = roleAutoDao.count();
        if(roles == 0){
            createRole(Role.ROLE_ADMIN);
            createRole(Role.ROLE_OFFICE,
                    Privilige.PRIV_CREATE_CLASS,
                    Privilige.PRIV_CREATE_STUDENT,
                    Privilige.PRIV_ETERNAL_QUIZ_STATISTICS_OF_ANY_STUDENT);
            createRole(Role.ROLE_TEACHER, 
                    Privilige.PRIV_CREATE_QUESTION, 
                    Privilige.PRIV_CREATE_TEST,
                    Privilige.PRIV_READ_QUESTION,
                    Privilige.PRIV_CRUD_ETERNAL_QUIZ,
                    Privilige.PRIV_ETERNAL_QUIZ_STATISTICS_OF_ANY_STUDENT);
            createRole(Role.ROLE_STUDENT,
                    Privilige.PRIV_READ_QUESTION,
                    Privilige.PRIV_OWN_ETERNAL_QUIZ_STATISTICS);
            LOG.debug("Roles created.");
        }
    }
    
    private void createRole(String... strs) {
        Role role = new Role(strs[0]);
        for (int i = 1; i < strs.length; i++) {
            String privName = strs[i];
            Privilige priv = priviligeAutoDao.findByName(privName);
            priv.addRole(role);
        }
        roleAutoDao.save(role);
    }

    private void createUsers() {
        long nrOfUsers = userAutoDao.count();
        
        if (nrOfUsers == 0) {
            Role teacherRole = roleAutoDao.findByName(Role.ROLE_TEACHER);
            Role adminRole = roleAutoDao.findByName(Role.ROLE_ADMIN);
            Role officeRole = roleAutoDao.findByName(Role.ROLE_OFFICE);
            User admin = new User();
            admin.setLoginName("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmailAddress("admin@progmatic.hu");
            teacherRole.addUser(admin);
            adminRole.addUser(admin);
            officeRole.addUser(admin);
            userAutoDao.save(admin);
            LOG.debug("Admin user created.");
        }
    }
}
