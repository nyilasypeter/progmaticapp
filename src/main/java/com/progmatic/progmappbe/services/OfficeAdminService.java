package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.aoutodaos.RoleAutoDao;
import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.helpers.MailHelper;
import com.progmatic.progmappbe.helpers.SecHelper;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OfficeAdminService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OfficeAdminService self;

    private DozerBeanMapper mapper;

    private PasswordEncoder passwordEncoder;

    private RoleAutoDao roleAutoDao;

    private MailHelper mailHelper;

    public OfficeAdminService(DozerBeanMapper mapper,
                              PasswordEncoder passwordEncoder,
                              RoleAutoDao roleAutoDao,
                              MailHelper mailHelper) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.roleAutoDao = roleAutoDao;
        this.mailHelper = mailHelper;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public EntityCreationResult createSchoolClass(SchoolClassDTO schoolClass){
        if(StringUtils.isNotBlank(schoolClass.getId())){
            if(em.find(SchoolClass.class, schoolClass.getId()) != null){
                return new EntityCreationResult(false, null, "Class with this id already exists");
            }
        }
        SchoolClass sc = mapper.map(schoolClass, SchoolClass.class);
        em.persist(sc);
        return new EntityCreationResult(true, schoolClass.getId(), null);
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public BasicResult assignStudentToClass(StudentListDto students, String classId){
        SchoolClass schoolClass = em.find(SchoolClass.class, classId);
        Set<User> newlyAddedStudents = new HashSet<>();
        StringBuilder errorString = new StringBuilder("");
        for (String studId: students.getIdList()) {
            User student = em.find(User.class, studId);
            if(student == null){
                errorString.append(String.format("User with id: %s is not found in database. ", studId));
            }
            else if(!SecHelper.hasRole(student, Role.ROLE_STUDENT)){
                errorString.append(String.format("User: %s is not a student. Only students can be added to a class. ", studId));
            }
            else if(schoolClass.getStudents().stream().filter(st -> st.getId().equals(student.getId())).count() > 0){
                errorString.append(String.format("Student: %s is already in this class: %s. ", studId, classId));
            }
            else{
                newlyAddedStudents.add(student);
                schoolClass.addStudent(student);
            }

        }
        self.createEternalQuizAnswers(schoolClass, newlyAddedStudents);
        if(errorString.length()!=0){
            errorString.append("Besides these problems all the other operations were successfull");
        }
        return new BasicResult(true, errorString.toString());
    }

    @Transactional
    public void createEternalQuizAnswers(SchoolClass schoolClass, Set<User> students){
        Set<EternalQuiz> eternalQuizes = schoolClass.getEternalQuizes();
        for (EternalQuiz eternalQuiz : eternalQuizes) {
            Set<Question> questions = eternalQuiz.getQuestions();
            for (User student : students) {
                for (Question question : questions) {
                    EternalQuizAnswer ea = new EternalQuizAnswer();
                    ea.setHasAnswer(false);
                    ea.setQuestion(question);
                    ea.setStudent(student);
                    em.persist(ea);
                }
            }
        }


    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    public EntityCreationResult createStudent(UserDTO studentDTO){
        User user = em.find(User.class, studentDTO.getLoginName());
        if(user != null){
            return new EntityCreationResult(false, null, "User with this login name already exists.");
        }
        studentDTO.getRoles().clear();
        User student = mapper.map(studentDTO, User.class);
        student.setId(studentDTO.getLoginName());
        //Role studentRole = roleAutoDao.findByName(Role.ROLE_STUDENT);
        Role studentRole = em.createQuery("select r from Role r where r.name = :rolename", Role.class)
                .setParameter("rolename", Role.ROLE_STUDENT)
                .getSingleResult();
        studentRole.addUser(student);
        if(StringUtils.isNotBlank(studentDTO.getPassword())){
            student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }
        else{
            student.setEnabled(false);
            String token = UUID.randomUUID().toString();
            student.setRegistrationToken(token);
            student.setRegistrationTokenValidTo(LocalDateTime.now().plusDays(1));
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put(MailHelper.RECIPIENT_KEY, student);
            templateModel.put(MailHelper.TOKEN_KEY, token);
            mailHelper.sendMailByTemplate(MailHelper.MAIL_TEMPLATE_STUDENT_REGISTRATION, studentDTO.getEmailAddress(), templateModel);
        }
        em.persist(student);
        return new EntityCreationResult(true, student.getId(), null);
    }
}
