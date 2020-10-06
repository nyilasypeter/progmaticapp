package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.aoutodaos.RoleAutoDao;
import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.schoolclass.SchoolClassDTO;
import com.progmatic.progmappbe.dtos.template.MailTemplateDTO;
import com.progmatic.progmappbe.dtos.user.*;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.helpers.MailHelper;
import com.progmatic.progmappbe.helpers.ResultBuilder;
import com.progmatic.progmappbe.helpers.SecHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    private ResultBuilder resultBuilder;


    @Autowired
    public OfficeAdminService(DozerBeanMapper mapper,
                              PasswordEncoder passwordEncoder,
                              RoleAutoDao roleAutoDao,
                              MailHelper mailHelper,
                              ResultBuilder resultBuilder) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.roleAutoDao = roleAutoDao;
        this.mailHelper = mailHelper;
        this.resultBuilder = resultBuilder;
    }


    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public EntityCreationResult createSchoolClass(SchoolClassDTO schoolClass) {
        if (StringUtils.isNotBlank(schoolClass.getId())) {
            if (em.find(SchoolClass.class, schoolClass.getId()) != null) {
                EntityCreationResult er = resultBuilder.errorEntityCreateResult(
                        "progmapp.error.idalreadyexists",
                        schoolClass.getId(),
                        resultBuilder.translate("progmapp.entity.shcoolclass"));
                return er;
            }
        }
        SchoolClass sc = mapper.map(schoolClass, SchoolClass.class);
        em.persist(sc);
        return resultBuilder.okEntityCreateResult(sc);
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public List<SchoolClassDTO> searchClass(SchoolClassDTO classFilter) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        BooleanBuilder whereCondition = new BooleanBuilder();
        QSchoolClass qSchoolClass = QSchoolClass.schoolClass;
        if (StringUtils.isNotBlank(classFilter.getId())) {
            whereCondition.and(qSchoolClass.id.eq(classFilter.getId()));
        }
        if (classFilter.getActive() != null) {
            if (classFilter.getActive()) {
                whereCondition.and(qSchoolClass.isActive.isTrue());
            } else {
                whereCondition.and(qSchoolClass.isActive.isFalse());
            }
        }
        if (classFilter.getYear() != null) {
            whereCondition.and(qSchoolClass.year.eq(classFilter.getYear()));
        }
        if (classFilter.getSemester() != null) {
            whereCondition.and(qSchoolClass.semester.eq(classFilter.getSemester()));
        }


        List<SchoolClass> schoolClasses = queryFactory.selectFrom(qSchoolClass)
                .where(whereCondition)
                .fetch();

        List<SchoolClassDTO> ret = new ArrayList<>();
        for (SchoolClass schoolClass : schoolClasses) {
            SchoolClassDTO ur = mapper.map(schoolClass, SchoolClassDTO.class);
            ret.add(ur);
        }
        return ret;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public BasicResult assignStudentToClass(StudentListDto students, String classId) {
        SchoolClass schoolClass = em.find(SchoolClass.class, classId);
        if (schoolClass == null) {
            return resultBuilder.errorEntityCreateResult(
                    "progmapp.error.iddoesnotexist",
                    schoolClass.getId(),
                    resultBuilder.translate("progmapp.entity.shcoolclass"));
        }
        Set<User> newlyAddedStudents = new HashSet<>();
        BasicResult ret = new BasicResult();
        //StringBuilder errorString = new StringBuilder("");
        for (String studId : students.getIdList()) {
            User student = em.find(User.class, studId);
            if (student == null) {
                ret.addNote(resultBuilder.translate("progmapp.error.iddoesnotexist", studId, resultBuilder.translate("progmapp.entity.user")));
            } else if (!SecHelper.hasRole(student, Role.ROLE_STUDENT)) {
                ret.addNote(resultBuilder.translate("progmapp.warning.usernotstudent", studId));
            } else if (schoolClass.getStudents().stream().filter(st -> st.getId().equals(student.getId())).count() > 0) {
                ret.addNote(resultBuilder.translate("progmapp.warning.studentalreadyinclass", studId, classId));
            } else {
                newlyAddedStudents.add(student);
                schoolClass.addStudent(student);
            }

        }
        self.createEternalQuizAnswers(schoolClass, newlyAddedStudents);
        ret.setSuccessFullResult(true);
        return ret;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public BasicResult removeStudentsFromClass(String studId, String classId) {
        SchoolClass schoolClass = em.find(SchoolClass.class, classId);
        if (schoolClass == null) {
            return resultBuilder.errorEntityCreateResult(
                    "progmapp.error.iddoesnotexist",
                    schoolClass.getId(),
                    resultBuilder.translate("progmapp.entity.shcoolclass"));
        }
        BasicResult ret = new BasicResult();
        User student = em.find(User.class, studId);
        if (student == null) {
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", studId, resultBuilder.translate("progmapp.entity.user"));
        } else if (!SecHelper.hasRole(student, Role.ROLE_STUDENT)) {
            return resultBuilder.errorResult("progmapp.warning.usernotstudent", studId);
        } else if (schoolClass.getStudents().stream().filter(st -> st.getId().equals(student.getId())).count() == 0) {
            return resultBuilder.errorResult("progmapp.warning.studentnotinclass", studId, classId);
        } else if (hasCompletedEternalQuizQuestion(student)) {
            return resultBuilder.errorResult("progmapp.warning.studenthasCompletedEternalQuizQuestion", studId, classId);
        } else {
            Iterator<User> iterator = schoolClass.getStudents().iterator();
            while (iterator.hasNext()) {
                User next = iterator.next();
                if (next.getId().equals(studId)) {
                    iterator.remove();
                }
            }

        }
        deleteEternalQuizAnswers(schoolClass, student);
        ret.setSuccessFullResult(true);
        return ret;
    }

    private void deleteEternalQuizAnswers(SchoolClass schoolClass, User deletedStudent) {
        List<EternalQuizAnswer> eqAnswers = em.createQuery("select e from EternalQuizAnswer e where e.student.id = :studentId", EternalQuizAnswer.class)
                .setParameter("studentId", deletedStudent.getId())
                .getResultList();
        for (EternalQuizAnswer eqAnswer : eqAnswers) {
            em.remove(eqAnswer);
        }
    }

    private boolean hasCompletedEternalQuizQuestion(User student) {
        List<EternalQuizAnswer> completedQuestions = em.createQuery("select e from EternalQuizAnswer e where e.student.id = :studentId and e.hasAnswer = TRUE ", EternalQuizAnswer.class)
                .setParameter("studentId", student.getId())
                .setMaxResults(1)
                .getResultList();
        return !completedQuestions.isEmpty();
    }

    @Transactional
    public void createEternalQuizAnswers(SchoolClass schoolClass, Set<User> students) {
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
    public EntityCreationResult createStudent(UserDTO studentDTO) {
        return createUser(studentDTO, true);
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_USER + "')")
    public EntityCreationResult createUser(UserDTO studentDTO) {
        return createUser(studentDTO, false);
    }

    private EntityCreationResult createUser(UserDTO userDTO, boolean isStudent) {
        User user = em.find(User.class, userDTO.getLoginName());
        if (user != null) {
            return resultBuilder.errorEntityCreateResult(
                    "progmapp.error.idalreadyexists",
                    userDTO.getLoginName(),
                    resultBuilder.translate("progmapp.entity.user"));
        }
        if (isStudent) {
            userDTO.getRoles().clear();
        }
        User newUser = mapper.map(userDTO, User.class);
        newUser.setId(userDTO.getLoginName());
        if (isStudent) {
            Role studentRole = roleAutoDao.findByName(Role.ROLE_STUDENT);
            studentRole.addUser(newUser);
        } else {
            for (RoleDTO rdto : userDTO.getRoles()) {
                Role role = roleAutoDao.findByName(rdto.getName());
                role.addUser(newUser);
            }
        }
        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            newUser.setEnabled(false);
            setRegLinkAndSendMail(newUser);
        }
        em.persist(newUser);
        return resultBuilder.okEntityCreateResult(newUser);
    }

    private void setRegLinkAndSendMail(User newUser) {
        String token = UUID.randomUUID().toString();
        newUser.setRegistrationToken(token);
        newUser.setRegistrationTokenValidTo(LocalDateTime.now().plusDays(1));
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put(MailHelper.RECIPIENT_KEY, newUser);
        templateModel.put(MailHelper.TOKEN_KEY, token);
        mailHelper.sendMailByTemplate(MailHelper.MAIL_TEMPLATE_STUDENT_REGISTRATION, newUser.getEmailAddress(), templateModel);
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    @Transactional
    public BasicResult updateRegistrationLink(String userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", userId, resultBuilder.translate("progmapp.entity.user"));
        }
        if (user.getRegistrationToken() == null) {
            return resultBuilder.errorResult("progmapp.error.useraleradyregistered");
        }
        setRegLinkAndSendMail(user);
        return resultBuilder.okResult();
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    public List<UserSearchResponseDTO> searchUser(UserSearchRequestDTO requestDTO) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        BooleanBuilder whereCondition = new BooleanBuilder();
        QUser qUser = QUser.user;
        QRole qRole = QRole.role;
        QPrivilige qPrivilige = QPrivilige.privilige;
        if (StringUtils.isNotBlank(requestDTO.getName())) {
            whereCondition.and(qUser.name.contains(requestDTO.getName()));
        }
        if (requestDTO.isHasUnfinishedRegistration() != null) {
            if (requestDTO.isHasUnfinishedRegistration()) {
                whereCondition.and(qUser.registrationToken.isNotNull());
            } else {
                whereCondition.and(qUser.registrationToken.isNull());
            }
        }
        if (requestDTO.isStudent() != null) {
            if (requestDTO.isStudent()) {
                whereCondition.and(qRole.name.eq("student"));
            } else {
                whereCondition.and(qRole.name.ne("student"));
            }
        }

        List<User> userList = queryFactory.selectFrom(qUser)
                .leftJoin(qUser.classes).fetchJoin()
                .leftJoin(qUser.roles, qRole).fetchJoin()
                .leftJoin(qRole.priviliges, qPrivilige).fetchJoin()
                .where(whereCondition)
                .distinct()
                .fetch();

        List<UserSearchResponseDTO> ret = new ArrayList<>();
        for (User user : userList) {
            UserSearchResponseDTO ur = mapper.map(user, UserSearchResponseDTO.class);
            ur.setHasUnfinishedRegistration(user.getRegistrationToken() != null);
            ret.add(ur);
        }
        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    @Transactional
    public List<MailTemplateDTO> getMailTemplates() {
        List<MailTemplate> resultList = em.createQuery("select mt from MailTemplate  mt", MailTemplate.class)
                .getResultList();
        List<MailTemplateDTO> ret = new ArrayList<>();
        for (MailTemplate mailTemplate : resultList) {
            ret.add(mapper.map(mailTemplate, MailTemplateDTO.class));
        }
        return ret;
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    @Transactional
    public BasicResult updateMailTemplate(MailTemplateDTO mailTemplateDTO) {
        if(StringUtils.isBlank(mailTemplateDTO.getId())){
            return resultBuilder.errorResult("porogmapp.error.mailtemplate.idmandatory");
        }
        MailTemplate mailTemplate = em.find(MailTemplate.class, mailTemplateDTO.getId());
        if(mailTemplate == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", mailTemplateDTO.getId(), resultBuilder.translate("progmapp.entity.mailtemplate"));
        }
        mapper.map(mailTemplateDTO, mailTemplate);
        return resultBuilder.okResult();
    }
}
