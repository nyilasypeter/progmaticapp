package com.progmatic.progmappbe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.quizresponse.AnswerResponseDTO;
import com.progmatic.progmappbe.dtos.quizresponse.PossibleAnswerResponseDTO;
import com.progmatic.progmappbe.entities.enums.FeedbackType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        studentListDto.getIdList().add("student");

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
        Assert.assertEquals((Integer) 20, stat.getNrOfAllQuestions());
        Assert.assertEquals((Integer) 0, stat.getNrOfRightAnswers());
        Assert.assertEquals((Integer) 0, stat.getNrOfBadAnswers());
        Assert.assertEquals((Double) 0d, stat.getAchievedPercentage());
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
        Assert.assertEquals(2, qdto.getPossibleAnswers().size());
        qdto.getPossibleAnswers().get(0).getPossibleAnswerValues().forEach(pv -> Assert.assertNull(pv.getIsRightAnswer()));

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
        Assert.assertEquals((Integer) 20, stat.getNrOfAllQuestions());
        Assert.assertEquals((Integer) 1, stat.getNrOfRightAnswers());
        Assert.assertEquals((Integer) 0, stat.getNrOfBadAnswers());
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

    /*With anonymous user we should not be able to create it*/
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
