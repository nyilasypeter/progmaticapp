package com.progmatic.progmappbe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.dtos.EntityCreationResult;
import com.progmatic.progmappbe.dtos.eternalquiz.*;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerDTO;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerValueDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionDTO;
import com.progmatic.progmappbe.dtos.schoolclass.SchoolClassDTO;
import com.progmatic.progmappbe.dtos.user.StudentListDto;
import com.progmatic.progmappbe.dtos.user.UserDTO;
import com.progmatic.progmappbe.entities.enums.FeedbackType;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuizTestBase {

    protected QuestionDTO createQuestionDTO() {
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


        PossibleAnswerDTO po2 = createPossibleAnswerDTO(
                "Második legkedevesebb",
                createPossibleAnswerValueDTO("sör", false),
                createPossibleAnswerValueDTO("kenyér", false),
                createPossibleAnswerValueDTO("tökfőzelék", false),
                createPossibleAnswerValueDTO("méz", true));
        qdto.getPossibleAnswers().add(po2);
        return qdto;
    }

    protected EntityCreationResult createQuestionWithMockMvc(QuestionDTO qdto, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
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
        return res;
    }

    protected PossibleAnswerDTO createPossibleAnswerDTO(String text, PossibleAnswerValueDTO... values) {
        PossibleAnswerDTO po1 = new PossibleAnswerDTO();
        po1.setTextBefore(text);
        for (PossibleAnswerValueDTO possibleAnswerValueDTO : values) {
            po1.getPossibleAnswerValues().add(possibleAnswerValueDTO);
        }
        return po1;
    }

    protected PossibleAnswerValueDTO createPossibleAnswerValueDTO(String name, boolean isRight) {
        PossibleAnswerValueDTO ret = new PossibleAnswerValueDTO();
        ret.setText(name);
        ret.setIsRightAnswer(isRight);
        return ret;
    }

    protected PossibleAnswerValueDTO createPossibleAnswerValueDTO(String name, int order) {
        PossibleAnswerValueDTO ret = new PossibleAnswerValueDTO();
        ret.setText(name);
        ret.setRightOrder(order);
        return ret;
    }

    protected void createUserClassEquiz(String username, String classname, String equiname, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        createStudent(username, mockMvc, objectMapper);
        createClass(classname, mockMvc, objectMapper);


    }

    protected void createStudent(String userName, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        UserDTO student = new UserDTO();
        student.setId(userName);
        student.setLoginName(userName);
        student.setPassword(userName);
        student.setName(userName);
        student.setEmailAddress(userName + "@progmaticmail.com");

        mockMvc.perform(
                post("/student")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is(userName)));

    }

    protected void createClass(String className, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        SchoolClassDTO schoolClassDTO = new SchoolClassDTO();
        schoolClassDTO.setId(className);

        mockMvc.perform(
                post("/class")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schoolClassDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is(className)));
    }

    protected void createEternalQuizWithMockMvc(String quizId, MockMvc mockMvc, ObjectMapper objectMapper, String... questionids) throws Exception {
        List<String> questions = new ArrayList<>();
        for (String questionid : questionids) {
            questions.add(questionid);
        }
        createEternalQuizWithMockMvc(quizId, questions, mockMvc, objectMapper);
    }

    protected void createEternalQuizWithMockMvc(String quizId, List<String> questionIds, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
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


    protected void assignStudentToClass(String studentName, String className, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        StudentListDto studentListDto = new StudentListDto();
        studentListDto.setIdList(new HashSet<>());
        studentListDto.getIdList().add(studentName);

        mockMvc.perform(
                put("/class/" + className + "/students")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentListDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));
    }


    protected void assignEternalQuizToClass(String equizName, String className, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        EternalQuizToClassDTO eternalQuizToClassDTO = new EternalQuizToClassDTO();
        eternalQuizToClassDTO.setEternalQuizId(equizName);
        eternalQuizToClassDTO.setSchoolClassId(className);

        mockMvc.perform(
                put("/eternalquiz/quiz/class")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eternalQuizToClassDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)));
    }

    protected void assignQuestionToEternalQuiz(String eternalQuizId, String questionId, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
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

    protected QuestionDTO getNextEternalQuiz(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/eternalquiz/question"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.text", Matchers.notNullValue()))
                .andReturn();
        QuestionDTO qdto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), QuestionDTO.class);
        return qdto;
    }

    protected AnswerFeedbackDTO submitAnswer(AnswerResponseDTO resp, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post("/eternalquiz/answer")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resp)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8")), AnswerFeedbackDTO.class);
    }

}
