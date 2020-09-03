package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.aoutodaos.RoleAutoDao;
import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.entities.*;
import com.progmatic.progmappbe.helpers.MailHelper;
import com.progmatic.progmappbe.helpers.ResultBuilder;
import com.progmatic.progmappbe.helpers.SecHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    @Transactional
    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_CLASS + "')")
    public BasicResult assignStudentToClass(StudentListDto students, String classId) {
        SchoolClass schoolClass = em.find(SchoolClass.class, classId);
        if(schoolClass == null){
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

    private void setRegLinkAndSendMail(User newUser){
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
    public BasicResult updateRegistrationLink(String userId){
        User user = em.find(User.class, userId);
        if(user == null){
            return resultBuilder.errorResult("progmapp.error.iddoesnotexist", userId, resultBuilder.translate("progmapp.entity.user"));
        }
        if(user.getRegistrationToken() == null){
            return resultBuilder.errorResult("progmapp.error.useraleradyregistered");
        }
        setRegLinkAndSendMail(user);
        return resultBuilder.okResult();
    }

    @PreAuthorize("hasAuthority('" + Privilige.PRIV_CREATE_STUDENT + "')")
    public List<SearchUserResponseDTO> searchStudents(UserSearchRequestDTO requestDTO) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        BooleanBuilder whereCondition = new BooleanBuilder();
        QUser qUser = QUser.user;
        QRole qRole = QRole.role;
        if (StringUtils.isNotBlank(requestDTO.getName())) {
            whereCondition.and(qUser.name.contains(requestDTO.getName()));
        }
        if (requestDTO.isHasUnfinishedRegistration() != null) {
            if (requestDTO.isHasUnfinishedRegistration()) {
                whereCondition.and(qUser.registrationToken.isNotNull());
            }
            else{
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
                .where(whereCondition)
                .distinct()
                .fetch();

        List<SearchUserResponseDTO> ret = new ArrayList<>();
        for (User user : userList) {
            SearchUserResponseDTO ur = mapper.map(user, SearchUserResponseDTO.class);
            ur.setHasUnfinishedRegistration(user.getRegistrationToken() != null);
            ret.add(ur);
        }
        return ret;
    }
}
