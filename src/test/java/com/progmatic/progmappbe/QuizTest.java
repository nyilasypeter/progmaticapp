package com.progmatic.progmappbe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.eternalquiz.*;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerDTO;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerValueDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionDTO;
import com.progmatic.progmappbe.dtos.schoolclass.SchoolClassDTO;
import com.progmatic.progmappbe.dtos.user.StudentListDto;
import com.progmatic.progmappbe.dtos.user.UserSearchResponseDTO;
import com.progmatic.progmappbe.entities.enums.FeedbackType;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("admin")
    void createQuestion() throws Exception {
        QuestionDTO qdto = createQuestionDTO();
        qdto.setId("micimackoKedvence");
        mockMvc.perform(
                post("/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qdto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is("micimackoKedvence")));

        //with the same id we should not be able to create it
        mockMvc.perform(
                post("/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qdto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(false)));

        //
    }


    @Test
    @WithUserDetails("admin")
    void updateQuestion() throws Exception {
        QuestionDTO qdto = createQuestionDTO();
        qdto.setId("1");
        PossibleAnswerDTO mostFavoritepo = new PossibleAnswerDTO();
        mostFavoritepo.setTextBefore("Mi a legeslegeslegkedvesebb?");
        mostFavoritepo.setType(PossibleAnswerType.radioButtons);
        mostFavoritepo.setPossibleAnswerValues(new ArrayList<>());
        mostFavoritepo.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("méz", true));
        mostFavoritepo.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("csilipaprika", false));
        qdto.getPossibleAnswers().add(mostFavoritepo);
        mockMvc.perform(
                post("/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qdto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is("1")));

        //find question just created
        MvcResult mvcResult = mockMvc.perform(get("/question/1"))
                .andExpect(status().isOk())
                .andReturn();

        QuestionDTO qdto2 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), QuestionDTO.class);
        assertEquals(3, qdto2.getPossibleAnswers().size());
        //update some fields of question
        qdto2.setAdminDescription("modified description");
        qdto2.setText("modifiedtext");
        //delete Mi a legeslegeslegkedvesebb? Possible answer
        ListIterator<PossibleAnswerDTO> polit = qdto2.getPossibleAnswers().listIterator();
        while(polit.hasNext()){
            PossibleAnswerDTO next = polit.next();
            if(next.getTextBefore().equals("Mi a legeslegeslegkedvesebb?")){
                polit.remove();
            }
        }
        //update some fields of Első gkedevesebb PossibleAnswer
        PossibleAnswerDTO paDto = qdto2.getPossibleAnswers().stream().filter(pa -> pa.getTextBefore().equals("Első legkedevesebb")).findFirst().get();
        paDto.setTextBefore("1. legkedvesebb");
        PossibleAnswerValueDTO kenyér = paDto.getPossibleAnswerValues().stream().filter(pv -> pv.getText().equals("kenyér")).findFirst().get();
        kenyér.setText("zsömle");
        //delete a possible answer value (sör)
        ListIterator<PossibleAnswerValueDTO> lit = paDto.getPossibleAnswerValues().listIterator();
        while(lit.hasNext()){
            PossibleAnswerValueDTO next = lit.next();
            if(next.getText().equals("sör")){
                lit.remove();
            }
        }
        //add a new possible answer value (bor)
        paDto.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("bor", false));

        //add a new possible answer value to Második legkedevesebb
        PossibleAnswerDTO paDto2 = qdto2.getPossibleAnswers().stream().filter(pa -> pa.getTextBefore().equals("Második legkedevesebb")).findFirst().get();
        paDto2.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("spenót", false));

        PossibleAnswerDTO newpaDto = new PossibleAnswerDTO();
        newpaDto.setTextBefore("No és a harmadik?");
        newpaDto.setType(PossibleAnswerType.radioButtons);
        newpaDto.setPossibleAnswerValues(new ArrayList<>());
        newpaDto.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("méz", true));
        newpaDto.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("csilipaprika", false));
        qdto2.getPossibleAnswers().add(newpaDto);

        //
         mockMvc.perform(
                put("/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qdto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));

        mvcResult = mockMvc.perform(get("/question/1"))
                .andExpect(status().isOk())
                .andReturn();

        QuestionDTO qdto3 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), QuestionDTO.class);
        assertEquals("modifiedtext", qdto3.getText());
        assertEquals("modified description", qdto3.getAdminDescription());
        PossibleAnswerDTO possibleAnswerDTO = qdto3.getPossibleAnswers().stream().filter(pa -> pa.getTextBefore().equals("1. legkedvesebb")).findFirst().get();
        //check that one possibleAnswerValue was  deleted, one added
        assertEquals(4, possibleAnswerDTO.getPossibleAnswerValues().size());
        //check that sör was deleted
        assertFalse(possibleAnswerDTO.getPossibleAnswerValues().stream().filter(pv -> pv.getText().equals("sör")).findFirst().isPresent());
        //check that bor was added
        assertTrue(possibleAnswerDTO.getPossibleAnswerValues().stream().filter(pv -> pv.getText().equals("bor")).findFirst().isPresent());
        //check that kenyér was really changed to zsomle
        Optional<PossibleAnswerValueDTO> bread = possibleAnswerDTO.getPossibleAnswerValues().stream().filter(pv -> pv.getText().equals("kenyér")).findFirst();
        assertFalse(bread.isPresent());
        Optional<PossibleAnswerValueDTO> zsomle = possibleAnswerDTO.getPossibleAnswerValues().stream().filter(pv -> pv.getText().equals("zsömle")).findFirst();
        assertTrue(zsomle.isPresent());

        PossibleAnswerDTO possibleAnswerDTO2 = qdto3.getPossibleAnswers().stream().filter(pa -> pa.getTextBefore().equals("Második legkedevesebb")).findFirst().get();
        assertEquals(5, possibleAnswerDTO2.getPossibleAnswerValues().size());

        PossibleAnswerDTO possibleAnswerDTO3 = qdto3.getPossibleAnswers().stream().filter(pa -> pa.getTextBefore().equals("No és a harmadik?")).findFirst().get();
        assertEquals(2, possibleAnswerDTO3.getPossibleAnswerValues().size());

        //check that Mi a legeslegeslegkedvesebb? was indeed deleted
        assertFalse(qdto2.getPossibleAnswers().stream().filter(pa -> pa.getTextBefore().equals("Mi a legeslegeslegkedvesebb?")).findFirst().isPresent());
        

    }

    @Test
    @WithUserDetails("teacher")
    @Order(10)
    void createEternalQuiz() throws Exception {
        List<String> createdQuestionIds = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            createdQuestionIds.add(createQuestionWithMockMvc());
        }
        List<String> createdQuestionIds2 = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            createdQuestionIds2.add(createQuestionWithMockMvc());
        }
        createdQuestionIds.stream().forEach(System.out::println);
        createEternalQuizWithMockMvc("firstEternalQuiz", createdQuestionIds);
        createEternalQuizWithMockMvc("secondEternalQuiz", null);
        for (String s : createdQuestionIds2) {
            assignQuestionToEternalQuiz("firstEternalQuiz", s);
        }
    }


    private String createQuestionWithMockMvc() throws Exception {
        QuestionDTO qdto = createQuestionDTO();
        MvcResult mvcResult = mockMvc.perform(
                post("/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qdto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.notNullValue()))
                .andReturn();
        EntityCreationResult res = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), EntityCreationResult.class);
        return res.getIdOfCreatedEntity();
    }

    private void createEternalQuizWithMockMvc(String quizId, List<String> questionIds) throws Exception {
        EternalQuizDTO quizDTO = new EternalQuizDTO();
        if (questionIds != null) {
            questionIds.stream().forEach(qid -> quizDTO.getQuestionIds().add(qid));
        }
        quizDTO.setId(quizId);
        mockMvc.perform(
                post("/eternalquiz")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is(quizId)));
    }

    private void assignQuestionToEternalQuiz(String eternalQuizId, String questionId) throws Exception {
        EternalQuizToQuestionDTO dto = new EternalQuizToQuestionDTO();
        dto.setEternalQuizId(eternalQuizId);
        dto.setQuestionId(questionId);
        mockMvc.perform(
                put("/eternalquiz/quiz/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));
    }

    @Test
    @WithUserDetails("officeUser")
    @Order(11)
    void createClass() throws Exception {
        SchoolClassDTO schoolClassDTO = new SchoolClassDTO();
        schoolClassDTO.setId("progmatic_2020_1");

        mockMvc.perform(
                post("/class")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schoolClassDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is("progmatic_2020_1")));
    }

    @Test
    @WithUserDetails("student")
    void studentCanotCreateClass() throws Exception {
        SchoolClassDTO schoolClassDTO = new SchoolClassDTO();
        schoolClassDTO.setId("progmatic_2020_1");

        mockMvc.perform(
                post("/class")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schoolClassDTO)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithUserDetails("officeUser")
    @Order(12)
    void assignStudentToClass() throws Exception {
        StudentListDto studentListDto = new StudentListDto();
        studentListDto.setIdList(new HashSet<>());
        //check ExtraDataLoaderForTests.createUsers
        studentListDto.getIdList().add("student");
        studentListDto.getIdList().add("student2");

        mockMvc.perform(
                put("/class/progmatic_2020_1/students")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentListDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));
    }


    @Test
    @WithUserDetails("teacher")
    @Order(13)
    void assignEternalQuizToClass() throws Exception {
        EternalQuizToClassDTO eternalQuizToClassDTO = new EternalQuizToClassDTO();
        eternalQuizToClassDTO.setEternalQuizId("firstEternalQuiz");
        eternalQuizToClassDTO.setSchoolClassId("progmatic_2020_1");

        mockMvc.perform(
                put("/eternalquiz/quiz/class")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eternalQuizToClassDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));
    }

    @Test
    @WithUserDetails("student")
    @Order(15)
    void checkStatisticsBeforeAnyAnswer() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/eternalquiz/me/statistics"))
                .andExpect(status().isOk())
                .andReturn();
        EternalQuizStatisticDTO stat = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), EternalQuizStatisticDTO.class);
        assertEquals((Integer) 20, stat.getNrOfAllQuestions());
        assertEquals((Integer) 0, stat.getNrOfRightAnswers());
        assertEquals((Integer) 0, stat.getNrOfBadAnswers());
        assertEquals((Double) 0d, stat.getAchievedPercentage());
    }

    @Test
    @WithUserDetails("student")
    @Order(20)
    void solveEternalQuiz() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/eternalquiz/question"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.text", Matchers.notNullValue()))
                .andReturn();
        QuestionDTO qdto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), QuestionDTO.class);
        assertEquals(2, qdto.getPossibleAnswers().size());
        qdto.getPossibleAnswers().get(0).getPossibleAnswerValues().forEach(pv -> assertNull(pv.getIsRightAnswer()));

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(qdto.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();
        
        PossibleAnswerResponseDTO po1 = new PossibleAnswerResponseDTO();
        po1.setId(qdto.getPossibleAnswers().get(0).getId());
        PossibleAnswerValueDTO mez1 = qdto.getPossibleAnswers().get(0).getPossibleAnswerValues().stream().filter(pov -> pov.getText().equals("méz")).findFirst().get();
        po1.setSelectedAnswerIds(new ArrayList<>());
        po1.getSelectedAnswerIds().add(mez1.getId());
        possAnswers.add(po1);

        PossibleAnswerResponseDTO po2 = new PossibleAnswerResponseDTO();
        po2.setId(qdto.getPossibleAnswers().get(1).getId());
        PossibleAnswerValueDTO mez2 = qdto.getPossibleAnswers().get(1).getPossibleAnswerValues().stream().filter(pov -> pov.getText().equals("méz")).findFirst().get();
        po2.setSelectedAnswerIds(new ArrayList<>());
        po2.getSelectedAnswerIds().add(mez2.getId());
        possAnswers.add(po2);
        
        resp.setAnswers(possAnswers);
        mockMvc.perform(
                post("/eternalquiz/answer")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resp)))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", Matchers.is("rightAnswer")));
        

    }

    @Test
    @WithUserDetails("student")
    @Order(21)
    void checkStatisticsAfterOneRightAnswer() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/eternalquiz/me/statistics"))
                .andExpect(status().isOk())
                .andReturn();
        EternalQuizStatisticDTO stat = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), EternalQuizStatisticDTO.class);
        assertEquals((Integer) 20, stat.getNrOfAllQuestions());
        assertEquals((Integer) 1, stat.getNrOfRightAnswers());
        assertEquals((Integer) 0, stat.getNrOfBadAnswers());
    }

    @Test
    @WithUserDetails("officeUser")
    @Order(25)
    void checkRemoveStudentFromClass() throws Exception{


        //both student and student2 should be in their class
        MvcResult userGetResult = mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andReturn();
        List<UserSearchResponseDTO> userResp = objectMapper.readValue(userGetResult.getResponse().getContentAsString(Charset.forName("UTF-8")), new TypeReference<List<UserSearchResponseDTO>>() {});
        UserSearchResponseDTO student = userResp.stream().filter(u -> u.getId().equals("student")).findFirst().get();
        UserSearchResponseDTO student2 = userResp.stream().filter(u -> u.getId().equals("student2")).findFirst().get();

        assertEquals(1, student.getClasses().size());
        assertEquals("progmatic_2020_1", student.getClasses().get(0).getId());

        assertEquals(1, student2.getClasses().size());
        assertEquals("progmatic_2020_1", student2.getClasses().get(0).getId());

        //we should be able to delete student2
        mockMvc.perform(
                delete("/class/progmatic_2020_1/students/student2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));

        //we should not be able to delete student
        MvcResult mvcResult = mockMvc.perform(
                delete("/class/progmatic_2020_1/students/student")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(false)))
                .andReturn();
        BasicResult res = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), BasicResult.class);
        res.getErrorMessages().forEach(em -> System.out.println(em.getLocalizedMessage()));

        // student should be in his class and student 2 should not be there
        userGetResult = mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andReturn();
        userResp = objectMapper.readValue(userGetResult.getResponse().getContentAsString(Charset.forName("UTF-8")), new TypeReference<List<UserSearchResponseDTO>>() {});
        student = userResp.stream().filter(u -> u.getId().equals("student")).findFirst().get();
        student2 = userResp.stream().filter(u -> u.getId().equals("student2")).findFirst().get();

        assertEquals(1, student.getClasses().size());
        assertEquals("progmatic_2020_1", student.getClasses().get(0).getId());

        assertEquals(0, student2.getClasses().size());

    }


    @Test
    @WithUserDetails("student")
    @Order(15)
    void studentCannotCreateEternalQuiz() throws Exception {
        EternalQuizDTO quizDTO = new EternalQuizDTO();
        mockMvc.perform(
                post("/eternalquiz")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /*With anonymous user we should not be able to create a question*/
    @Test
    void createTestWithoutPrivilige() throws Exception {
        QuestionDTO qdto = createQuestionDTO();
        mockMvc.perform(
                post("/question")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qdto)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    private QuestionDTO createQuestionDTO() {
        QuestionDTO qdto = new QuestionDTO();
        qdto.setExplanationAfter("Igen, igen. Megmondtam, megmondtam.");
        qdto.setAdminDescription("Micimackó és a méz");
        qdto.setFeedbackType(FeedbackType.trueFalseFeedback);
        qdto.setText("Melyik micimackó két legkedvesebb étele?");
        PossibleAnswerDTO po1 = new PossibleAnswerDTO();
        po1.setTextBefore("Első legkedevesebb");
        po1.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("sör", false));
        po1.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("kenyér", false));
        po1.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("tökfőzelék", false));
        po1.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("méz", true));
        qdto.getPossibleAnswers().add(po1);


        PossibleAnswerDTO po2 = new PossibleAnswerDTO();
        po2.setTextBefore("Második legkedevesebb");
        po2.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("sör", false));
        po2.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("kenyér", false));
        po2.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("tökfőzelék", false));
        po2.getPossibleAnswerValues().add(createPossibleAnswerValueDTO("méz", true));
        qdto.getPossibleAnswers().add(po2);
        return qdto;
    }

    private PossibleAnswerValueDTO createPossibleAnswerValueDTO(String name, boolean isRight) {
        PossibleAnswerValueDTO ret = new PossibleAnswerValueDTO();
        ret.setText(name);
        ret.setIsRightAnswer(isRight);
        return ret;
    }
}
