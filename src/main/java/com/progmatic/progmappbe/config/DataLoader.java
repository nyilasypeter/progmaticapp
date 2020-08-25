/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import com.progmatic.progmappbe.aoutodaos.PriviligeAutoDao;
import com.progmatic.progmappbe.aoutodaos.RoleAutoDao;
import com.progmatic.progmappbe.aoutodaos.UserAutoDao;
import com.progmatic.progmappbe.entities.Privilige;
import com.progmatic.progmappbe.entities.Role;
import com.progmatic.progmappbe.entities.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public DataLoader(UserAutoDao userAutoDao, PasswordEncoder passwordEncoder,
                      PriviligeAutoDao priviligeAutoDao, RoleAutoDao roleAutoDao,
                      @Value("progmatic.admin.default.password") String adminPassword) {
        this.userAutoDao = userAutoDao;
        this.passwordEncoder = passwordEncoder;
        this.priviligeAutoDao = priviligeAutoDao;
        this.roleAutoDao = roleAutoDao;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createPriviliges();
        createRoles();
        createUsers();
    }

    private void createPriviliges() {
        long priviliges = priviligeAutoDao.count();
        if(priviliges == 0){
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_CREATE_QUESTION));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_CREATE_TEST));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_START_TEST));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_READ_QUESTION));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_CREATE_CLASS));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_CREATE_STUDENT));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_CREATE_USER));
            priviligeAutoDao.save(new Privilige(Privilige.PRIV_CRUD_ETERNAL_QUIZ));
            LOG.debug("Priviliges created.");
        }
    }

    private void createRoles() {
        long priviliges = roleAutoDao.count();
        if(priviliges == 0){
            createRole(Role.ROLE_ADMIN);
            createRole(Role.ROLE_OFFICE,
                    Privilige.PRIV_CREATE_CLASS,
                    Privilige.PRIV_CREATE_STUDENT);
            createRole(Role.ROLE_TEACHER, 
                    Privilige.PRIV_CREATE_QUESTION, 
                    Privilige.PRIV_CREATE_TEST,
                    Privilige.PRIV_READ_QUESTION,
                    Privilige.PRIV_CRUD_ETERNAL_QUIZ);
            createRole(Role.ROLE_STUDENT,
                    Privilige.PRIV_READ_QUESTION);
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
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmailAddress("admin@progmatic.hu");
            teacherRole.addUser(admin);
            adminRole.addUser(admin);
            officeRole.addUser(admin);
            userAutoDao.save(admin);
            LOG.debug("Admin user created.");
        }
    }
}
