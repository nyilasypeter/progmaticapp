package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.aoutodaos.RoleAutoDao;
import com.progmatic.progmappbe.dtos.SchoolClassDTO;
import com.progmatic.progmappbe.dtos.StudentListDto;
import com.progmatic.progmappbe.dtos.UserDTO;
import com.progmatic.progmappbe.entities.Privilige;
import com.progmatic.progmappbe.entities.Role;
import com.progmatic.progmappbe.entities.SchoolClass;
import com.progmatic.progmappbe.entities.User;
import com.progmatic.progmappbe.helpers.SecHelper;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class OfficeAdminService {
    @PersistenceContext
    EntityManager em;

    DozerBeanMapper mapper;

    PasswordEncoder passwordEncoder;

    private RoleAutoDao roleAutoDao;

    public OfficeAdminService(DozerBeanMapper mapper, PasswordEncoder passwordEncoder, RoleAutoDao roleAutoDao) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.roleAutoDao = roleAutoDao;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public SchoolClassDTO createSchoolClass(SchoolClassDTO schoolClass){
        SchoolClass sc = mapper.map(schoolClass, SchoolClass.class);
        em.persist(sc);
        return mapper.map(sc, SchoolClassDTO.class);
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public void assignStudentToClass(StudentListDto students, String classId){
        SchoolClass schoolClass = em.find(SchoolClass.class, classId);
        for (String studId: students.getIdList()) {
            User student = em.find(User.class, studId);
            if(!SecHelper.hasRole(student, Role.ROLE_STUDENT)){
                throw new RuntimeException("One of the users is not a student. Only students can be adeded to a class");
            }
            schoolClass.addStudent(student);
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    public UserDTO createStudent(UserDTO studentDTO){
        studentDTO.getRoles().clear();
        User student = mapper.map(studentDTO, User.class);
        student.setId(studentDTO.getLoginName());
        Role studentRole = roleAutoDao.findByName(Role.ROLE_STUDENT);
        studentRole.addUser(student);
        if(StringUtils.isNotBlank(studentDTO.getPassword())){
            student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }
        else{
            student.setEnabled(false);
            //TODO: send e-mail to student with link to complete registration
        }
        em.persist(student);
        return mapper.map(student, UserDTO.class);
    }
}
