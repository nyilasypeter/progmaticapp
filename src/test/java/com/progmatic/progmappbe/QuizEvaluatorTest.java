package com.progmatic.progmappbe.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.QuizTestBase;
import com.progmatic.progmappbe.dtos.eternalquiz.AnswerFeedbackDTO;
import com.progmatic.progmappbe.dtos.eternalquiz.AnswerResponseDTO;
import com.progmatic.progmappbe.dtos.eternalquiz.PossibleAnswerResponseDTO;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerDTO;
import com.progmatic.progmappbe.dtos.quiz.PossibleAnswerValueDTO;
import com.progmatic.progmappbe.dtos.quiz.QuestionDTO;
import com.progmatic.progmappbe.entities.enums.AnswerEvaulationResult;
import com.progmatic.progmappbe.entities.enums.FeedbackType;
import com.progmatic.progmappbe.entities.enums.PossibleAnswerType;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizEvaluatorTest extends QuizTestBase {

    public static final String STUDENT_PREFIX = "student_";
    public static final String QUESTION_PREFIX = "question_";
    public static final String CLASS_PREFIX = "class_";
    public static final String EQUIZ_PREFIX = "equiz_";
    private static final String SOURCE_CODE_QUESTION_ID = "sourceCodeQuestion1";
    private static final String QUESTION_WTIH_TWO_POOSS_ANSWER = "twopossanswQuestion1";
    private static final String SOURCE_CODE_WITH_EVAL_QUESTION_ID = "sourceCodeWithEvalQuestion1";

    private static final String MIN_SEARCH_GOOD_STANDARD_ORDER = "package org.progmatic.sourcequiz.classtotest;\n" +
            "\n" +
            "public class MinFinder {\n" +
            "\n" +
            "    public MinFinder() {\n" +
            "    }\n" +
            "\n" +
            "    public Integer findMin(int[] nums){\n" +
            "        if(nums.length == 0){\n" +
            "            return null;\n" +
            "        }\n" +
            "        int min = nums[0];\n" +
            "        for (int i = 1; i < nums.length; i++) {\n" +
            "            if(nums[i] < min){\n" +
            "                min = nums[i];\n" +
            "            }\n" +
            "        }\n" +
            "        return min;\n" +
            "    }\n" +
            "\n" +
            "}\n";

    private static final String MIN_SEARCH_GOOD_NON_STANDARD_ORDER = "package org.progmatic.sourcequiz.classtotest;\n" +
            "\n" +
            "public class MinFinder {\n" +
            "\n" +
            "    public MinFinder() {\n" +
            "    }\n" +
            "\n" +
            "    public Integer findMin(int[] nums){\n" +
            "        int min = nums[0];\n" +
            "        if(nums.length == 0){\n" +
            "            return null;\n" +
            "        }\n" +
            "        for (int i = 1; i < nums.length; i++) {\n" +
            "            if(nums[i] < min){\n" +
            "                min = nums[i];\n" +
            "            }\n" +
            "        }\n" +
            "        return min;\n" +
            "    }\n" +
            "\n" +
            "}\n";
    private static final String MIN_SEARCH_BAD = "package org.progmatic.sourcequiz.classtotest;\n" +
            "\n" +
            "public class MinFinder {\n" +
            "\n" +
            "    public MinFinder() {\n" +
            "    }\n" +
            "\n" +
            "    public Integer findMin(int[] nums){\n" +
            "        int min = nums[0];\n" +
            "        if(nums.length == 0){\n" +
            "            return null;\n" +
            "        }\n" +
            "        for (int i = 1; i < nums.length; i++) {\n" +
            "            min = nums[i];\n" +
            "            if(nums[i] < min){\n" +

            "            }\n" +
            "        }\n" +
            "        return min;\n" +
            "    }\n" +
            "\n" +
            "}\n";

    private static final String MIN_SEARCH_COMPIL_ERROR = "package org.progmatic.sourcequiz.classtotest;\n" +
            "\n" +
            "public class MinFinder {\n" +
            "\n" +
            "    public MinFinder() {\n" +
            "    }\n" +
            "\n" +
            "    public Integer findMin(int[] nums){\n" +

            "        if(nums.length == 0){\n" +
            "            return null;\n" +
            "        }\n" +
            "        for (int i = 1; i < nums.length; i++) {\n" +
            "            min = nums[i];\n" +
            "            if(nums[i] < min){\n" +
            "        int min = nums[0];\n" +
            "            }\n" +
            "        }\n" +
            "        return min;\n" +
            "    }\n" +
            "\n" +
            "}\n";

    private static final String MIN_SEARCH_COMPIL_MISSING_ROW = "package org.progmatic.sourcequiz.classtotest;\n" +
            "\n" +
            "public class MinFinder {\n" +
            "\n" +
            "    public MinFinder() {\n" +
            "    }\n" +
            "\n" +
            "    public Integer findMin(int[] nums){\n" +

            "        if(nums.length == 0){\n" +
            "            return null;\n" +
            "        }\n" +
            "        for (int i = 1; i < nums.length; i++) {\n" +
            "            if(nums[i] < min){\n" +
            "        int min = nums[0];\n" +
            "            }\n" +
            "        }\n" +
            "        return min;\n" +
            "    }\n" +
            "\n" +
            "}\n";

    private static final String MIN_SEARCH_UNIT_TEST = "package org.progmatic.sourcequiz.test;\n" +
            "\n" +
            "import org.junit.Assert;\n" +
            "import org.junit.jupiter.api.Assertions;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import org.progmatic.sourcequiz.classtotest.MinFinder;\n" +
            "\n" +
            "public class MinFinderUnitTest {\n" +
            "    \n" +
            "    private MinFinder mf = new MinFinder();\n" +
            "    \n" +
            "    @Test\n" +
            "    void checkSolution() {\n" +
            "\n" +
            "\n" +
            "        Assert.assertEquals(Integer.valueOf(0), mf.findMin(new int[]{0, 1, 2, 3, 5}));\n" +
            "        Assertions.assertEquals(Integer.valueOf(0), mf.findMin(new int[]{0, 1, 2, 3, 5}));\n" +
            "        Assertions.assertEquals(Integer.valueOf(1), mf.findMin(new int[]{10, 9, 5, 3, 1}));\n" +
            "        Assertions.assertEquals(Integer.valueOf(-100), mf.findMin(new int[]{0, -3, 5, 8, -100, 100, 2}));\n" +
            "        Assertions.assertEquals(Integer.valueOf(1), mf.findMin(new int[]{1, 1, 1}));\n" +
            "        Assertions.assertEquals(Integer.valueOf(0), mf.findMin(new int[]{0}));\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void finalize() throws Throwable {\n" +
            "        //System.out.println(\"An instance of MinfinderUntiTest is about to be gc-d\");\n" +
            "        super.finalize();\n" +
            "    }\n" +
            "}\n";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithUserDetails("admin")
    @Order(10)
    void createOrderQuestion() throws Exception {
        QuestionDTO qdto = new QuestionDTO();
        String questionId = QUESTION_PREFIX + SOURCE_CODE_QUESTION_ID;
        String studentId = STUDENT_PREFIX + SOURCE_CODE_QUESTION_ID;
        String classId = CLASS_PREFIX + SOURCE_CODE_QUESTION_ID;
        String equizId = EQUIZ_PREFIX + SOURCE_CODE_QUESTION_ID;

        qdto.setId(questionId);
        qdto.setFeedbackType(FeedbackType.trueFalseFeedback);
        qdto.setText("text");
        qdto.getPossibleAnswers().add(
                createPossibleAnswerDTO(
                        "Rakd sorrendbe",
                        createPossibleAnswerValueDTO("első", 1),
                        createPossibleAnswerValueDTO("első", 2),
                        createPossibleAnswerValueDTO("második", 3),
                        createPossibleAnswerValueDTO("harmadik", 4))
        );
        createQuestionWithMockMvc(qdto, mockMvc, objectMapper);
        createStudent(studentId, mockMvc, objectMapper);
        createClass(classId, mockMvc, objectMapper);
        assignStudentToClass(studentId, classId, mockMvc, objectMapper);
        createEternalQuizWithMockMvc(equizId, mockMvc, objectMapper, questionId);
        assignEternalQuizToClass(equizId, classId, mockMvc, objectMapper);

    }


    @Test
    @WithUserDetails("admin")
    @Order(10)
    void createCodeEvalQuestion() throws Exception {
        QuestionDTO qdto = new QuestionDTO();
        String questionId = QUESTION_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID;
        String studentId = STUDENT_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID;
        String classId = CLASS_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID;
        String equizId = EQUIZ_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID;

        qdto.setId(questionId);
        qdto.setFeedbackType(FeedbackType.trueFalseFeedback);
        qdto.setText("text");
        PossibleAnswerDTO possibleAnswerDTO = possibleAnswerFromSourceCode(MIN_SEARCH_GOOD_STANDARD_ORDER);
        possibleAnswerDTO.setUnitTestCode(MIN_SEARCH_UNIT_TEST);
        possibleAnswerDTO.setType(PossibleAnswerType.soruceCodeToOrder_EvalByRun);
        qdto.getPossibleAnswers().add(possibleAnswerDTO);
        createQuestionWithMockMvc(qdto, mockMvc, objectMapper);
        createStudent(studentId, mockMvc, objectMapper);
        createClass(classId, mockMvc, objectMapper);
        assignStudentToClass(studentId, classId, mockMvc, objectMapper);
        createEternalQuizWithMockMvc(equizId, mockMvc, objectMapper, questionId);
        assignEternalQuizToClass(equizId, classId, mockMvc, objectMapper);

    }

    private PossibleAnswerDTO possibleAnswerFromSourceCode(String sourceCode) {
        PossibleAnswerDTO ret = new PossibleAnswerDTO();
        String[] lines = sourceCode.split("\n");
        for (int i = 0; i < lines.length; i++) {
            PossibleAnswerValueDTO possibleAnswerValueDTO = createPossibleAnswerValueDTO(lines[i], i);
            ret.getPossibleAnswerValues().add(possibleAnswerValueDTO);
        }
        return ret;
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_QUESTION_ID)
    @Order(20)
    void solveOrderQuestionWell() throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(1, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO nextPossibleAnswer = nextEternalQuiz.getPossibleAnswers().get(0);
        assertEquals(4, nextPossibleAnswer.getPossibleAnswerValues().size());
        PossibleAnswerResponseDTO po1 = new PossibleAnswerResponseDTO();
        po1.setId(nextPossibleAnswer.getId());
        po1.setSelectedAnswerIds(new ArrayList<>(Arrays.asList(new String[]{"", "", "", ""})));

        boolean firstAdded = false;
        for (PossibleAnswerValueDTO possibleAnswerValue : nextPossibleAnswer.getPossibleAnswerValues()) {
            assertNull(possibleAnswerValue.getIsRightAnswer());
            assertNull(possibleAnswerValue.getIsRightAnswer());
            assertNull(possibleAnswerValue.getRightOrder());
            if(possibleAnswerValue.getText().equals("első")){
                if(!firstAdded) {
                    po1.getSelectedAnswerIds().set(0, possibleAnswerValue.getId());
                    firstAdded = true;
                }
                else{
                    po1.getSelectedAnswerIds().set(1, possibleAnswerValue.getId());
                }
            }
            else if(possibleAnswerValue.getText().equals("második")){
                po1.getSelectedAnswerIds().set(2, possibleAnswerValue.getId());
            }
            else if(possibleAnswerValue.getText().equals("harmadik")){
                po1.getSelectedAnswerIds().set(3, possibleAnswerValue.getId());
            }
        }
        assertEquals(4, po1.getSelectedAnswerIds().size());
        possAnswers.add(po1);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.rightAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID)
    @Order(20)
    void solveSourceCodeQuestionWell() throws Exception {
        AnswerFeedbackDTO answerFeedbackDTO = solveSourceCodeQuestion(MIN_SEARCH_GOOD_STANDARD_ORDER);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.rightAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID)
    @Order(21)
    void solveSourceCodeQuestionWellNonStandardOrder() throws Exception {
        AnswerFeedbackDTO answerFeedbackDTO = solveSourceCodeQuestion(MIN_SEARCH_GOOD_NON_STANDARD_ORDER);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.rightAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID)
    @Order(22)
    void solveSourceCodeQuestionWrong() throws Exception {
        AnswerFeedbackDTO answerFeedbackDTO = solveSourceCodeQuestion(MIN_SEARCH_BAD);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.falseAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID)
    @Order(22)
    void solveSourceCodeQuestionWrongNoCompile() throws Exception {
        AnswerFeedbackDTO answerFeedbackDTO = solveSourceCodeQuestion(MIN_SEARCH_COMPIL_ERROR);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.falseAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_WITH_EVAL_QUESTION_ID)
    @Order(22)
    void solveSourceCodeQuestionWrongMissingRow() throws Exception {
        AnswerFeedbackDTO answerFeedbackDTO = solveSourceCodeQuestion(MIN_SEARCH_COMPIL_MISSING_ROW);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.falseAnswer, answerFeedbackDTO.getResult());
    }

    AnswerFeedbackDTO solveSourceCodeQuestion(String solution) throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(1, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO nextPossibleAnswer = nextEternalQuiz.getPossibleAnswers().get(0);
        PossibleAnswerResponseDTO po1 = new PossibleAnswerResponseDTO();
        po1.setId(nextPossibleAnswer.getId());
        po1.setSelectedAnswerIds(new ArrayList<>());

        List<PossibleAnswerValueDTO> possibleAnswerValuesInQuestion = nextPossibleAnswer.getPossibleAnswerValues();


        PossibleAnswerDTO possibleAnswerDTO = possibleAnswerFromSourceCode(solution);
        int order = 0;
        for (PossibleAnswerValueDTO possibleAnswerValue : possibleAnswerDTO.getPossibleAnswerValues()) {
            String origId = getId(possibleAnswerValue.getText(), possibleAnswerValuesInQuestion);
            po1.getSelectedAnswerIds().add(origId);
        }
        possAnswers.add(po1);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        return answerFeedbackDTO;
    }



    private String getId(String text, List<PossibleAnswerValueDTO> possibleAnswerValuesInQuestion) {
        ListIterator<PossibleAnswerValueDTO> iter = possibleAnswerValuesInQuestion.listIterator();
        String id = "";
        while(iter.hasNext()){
            PossibleAnswerValueDTO next = iter.next();
            if(next.getText().trim().equals(text.trim())){
                id = next.getId();
                iter.remove();
                break;
            }
        }
        assertTrue(StringUtils.isNotBlank(id));
        return id;
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + SOURCE_CODE_QUESTION_ID)
    @Order(30)
    void solveOrderQuestionWrong() throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(1, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO nextPossibleAnswer = nextEternalQuiz.getPossibleAnswers().get(0);
        assertEquals(4, nextPossibleAnswer.getPossibleAnswerValues().size());
        PossibleAnswerResponseDTO poResp1 = new PossibleAnswerResponseDTO();
        poResp1.setId(nextPossibleAnswer.getId());
        poResp1.setSelectedAnswerIds(new ArrayList<>(Arrays.asList(new String[]{"", "", "", ""})));

        boolean firstAdded = false;
        for (PossibleAnswerValueDTO possibleAnswerValue : nextPossibleAnswer.getPossibleAnswerValues()) {
            assertNull(possibleAnswerValue.getIsRightAnswer());
            assertNull(possibleAnswerValue.getIsRightAnswer());
            assertNull(possibleAnswerValue.getRightOrder());
            if(possibleAnswerValue.getText().equals("első")){
                if(!firstAdded) {
                    poResp1.getSelectedAnswerIds().set(0, possibleAnswerValue.getId());
                    firstAdded = true;
                }
                else{
                    poResp1.getSelectedAnswerIds().set(1, possibleAnswerValue.getId());
                }
            }
            else if(possibleAnswerValue.getText().equals("második")){
                poResp1.getSelectedAnswerIds().set(3, possibleAnswerValue.getId());
            }
            else if(possibleAnswerValue.getText().equals("harmadik")){
                poResp1.getSelectedAnswerIds().set(2, possibleAnswerValue.getId());
            }
        }
        assertEquals(4, poResp1.getSelectedAnswerIds().size());
        possAnswers.add(poResp1);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.falseAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails("admin")
    @Order(11)
    void createQuestionWtihTwoPossAnswer() throws Exception {
        QuestionDTO qdto = new QuestionDTO();
        String questionId = QUESTION_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER;
        String studentId = STUDENT_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER;
        String classId = CLASS_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER;
        String equizId = EQUIZ_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER;

        qdto.setId(questionId);
        qdto.setFeedbackType(FeedbackType.trueFalseFeedback);
        qdto.setText("text");
        PossibleAnswerDTO pdto1 =  createPossibleAnswerDTO(
                "Melyik Micimackó kedvence?",
                createPossibleAnswerValueDTO("méz", true),
                createPossibleAnswerValueDTO("sör", false),
                createPossibleAnswerValueDTO("tej", false),
                createPossibleAnswerValueDTO("kenyér", false));
        pdto1.setOrder(1);
        qdto.getPossibleAnswers().add(pdto1);
        PossibleAnswerDTO pdto2 = createPossibleAnswerDTO(
                "Melyiket szereti Micimackó?",
                createPossibleAnswerValueDTO("méz", true),
                createPossibleAnswerValueDTO("sör", false),
                createPossibleAnswerValueDTO("tej", true),
                createPossibleAnswerValueDTO("kenyér", true));
        pdto2.setOrder(2);
        qdto.getPossibleAnswers().add(pdto2);
        createQuestionWithMockMvc(qdto, mockMvc, objectMapper);
        createStudent(studentId, mockMvc, objectMapper);
        createClass(classId, mockMvc, objectMapper);
        assignStudentToClass(studentId, classId, mockMvc, objectMapper);
        createEternalQuizWithMockMvc(equizId, mockMvc, objectMapper, questionId);
        assignEternalQuizToClass(equizId, classId, mockMvc, objectMapper);

    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER)
    @Order(20)
    void solveQuestionWtihTwoPossAnswerWell() throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(2, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO po1 = nextEternalQuiz.getPossibleAnswers().get(0);
        PossibleAnswerDTO po2 = nextEternalQuiz.getPossibleAnswers().get(1);

        assertEquals(4, po1.getPossibleAnswerValues().size());
        assertEquals(4, po2.getPossibleAnswerValues().size());
        PossibleAnswerResponseDTO poResp1 = new PossibleAnswerResponseDTO();
        poResp1.setId(po1.getId());
        PossibleAnswerValueDTO méz = po1.getPossibleAnswerValues().stream().filter(possibleAnswerValueDTO -> possibleAnswerValueDTO.getText().equals("méz")).findFirst().get();
        poResp1.setSelectedAnswerIds(new ArrayList<>());
        poResp1.getSelectedAnswerIds().add(méz.getId());

        PossibleAnswerResponseDTO poResp2 = new PossibleAnswerResponseDTO();
        poResp2.setId(po2.getId());
        poResp2.setSelectedAnswerIds(new ArrayList<>());
        List<PossibleAnswerValueDTO> foodsLovedByMicimacko = po2.getPossibleAnswerValues().stream().filter(possibleAnswerValueDTO -> !possibleAnswerValueDTO.getText().equals("sör")).collect(Collectors.toList());
        foodsLovedByMicimacko.stream().forEach(pv -> poResp2.getSelectedAnswerIds().add(pv.getId()));
        assertEquals(3, poResp2.getSelectedAnswerIds().size());

        possAnswers.add(poResp1);
        possAnswers.add(poResp2);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.rightAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER)
    @Order(30)
    void solveQuestionWtihTwoPossAnswerPariallyWell() throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(2, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO po1 = nextEternalQuiz.getPossibleAnswers().get(0);
        PossibleAnswerDTO po2 = nextEternalQuiz.getPossibleAnswers().get(1);

        assertEquals(4, po1.getPossibleAnswerValues().size());
        assertEquals(4, po2.getPossibleAnswerValues().size());
        PossibleAnswerResponseDTO poResp1 = new PossibleAnswerResponseDTO();
        poResp1.setId(po1.getId());
        PossibleAnswerValueDTO méz = po1.getPossibleAnswerValues().stream().filter(possibleAnswerValueDTO -> possibleAnswerValueDTO.getText().equals("méz")).findFirst().get();
        poResp1.setSelectedAnswerIds(new ArrayList<>());
        poResp1.getSelectedAnswerIds().add(méz.getId());

        PossibleAnswerResponseDTO poResp2 = new PossibleAnswerResponseDTO();
        poResp2.setId(po2.getId());
        poResp2.setSelectedAnswerIds(new ArrayList<>());
        List<PossibleAnswerValueDTO> foodsLovedByMicimacko = po2.getPossibleAnswerValues().stream().filter(possibleAnswerValueDTO -> !possibleAnswerValueDTO.getText().equals("sör")).collect(Collectors.toList());
        foodsLovedByMicimacko.stream().limit(2).forEach(pv -> poResp2.getSelectedAnswerIds().add(pv.getId()));
        assertEquals(2, poResp2.getSelectedAnswerIds().size());

        possAnswers.add(poResp1);
        possAnswers.add(poResp2);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.partiallyRightAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER)
    @Order(40)
    void solveQuestionWtihTwoPossAnswerWrong() throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(2, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO po1 = nextEternalQuiz.getPossibleAnswers().get(0);
        PossibleAnswerDTO po2 = nextEternalQuiz.getPossibleAnswers().get(1);

        assertEquals(4, po1.getPossibleAnswerValues().size());
        assertEquals(4, po2.getPossibleAnswerValues().size());
        PossibleAnswerResponseDTO poResp1 = new PossibleAnswerResponseDTO();
        poResp1.setId(po1.getId());
        PossibleAnswerValueDTO méz = po1.getPossibleAnswerValues().stream().filter(possibleAnswerValueDTO -> possibleAnswerValueDTO.getText().equals("méz")).findFirst().get();
        poResp1.setSelectedAnswerIds(new ArrayList<>());
        poResp1.getSelectedAnswerIds().add(méz.getId());

        PossibleAnswerResponseDTO poResp2 = new PossibleAnswerResponseDTO();
        poResp2.setId(po2.getId());
        poResp2.setSelectedAnswerIds(new ArrayList<>());
        po2.getPossibleAnswerValues().stream().forEach(pv -> poResp2.getSelectedAnswerIds().add(pv.getId()));
        assertEquals(4, poResp2.getSelectedAnswerIds().size());

        possAnswers.add(poResp1);
        possAnswers.add(poResp2);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.falseAnswer, answerFeedbackDTO.getResult());
    }

    @Test
    @WithUserDetails(STUDENT_PREFIX + QUESTION_WTIH_TWO_POOSS_ANSWER)
    @Order(50)
    void solveQuestionWtihTwoPossAnswerWrongWithoutOnePossAnsw() throws Exception {
        QuestionDTO nextEternalQuiz = getNextEternalQuiz(mockMvc, objectMapper);
        assertEquals(2, nextEternalQuiz.getPossibleAnswers().size());

        AnswerResponseDTO resp = new AnswerResponseDTO();
        resp.setQuestionId(nextEternalQuiz.getId());
        List<PossibleAnswerResponseDTO> possAnswers = new ArrayList<>();

        PossibleAnswerDTO po1 = nextEternalQuiz.getPossibleAnswers().get(0);
        PossibleAnswerDTO po2 = nextEternalQuiz.getPossibleAnswers().get(1);

        assertEquals(4, po1.getPossibleAnswerValues().size());
        assertEquals(4, po2.getPossibleAnswerValues().size());
        PossibleAnswerResponseDTO poResp1 = new PossibleAnswerResponseDTO();
        poResp1.setId(po1.getId());
        PossibleAnswerValueDTO méz = po1.getPossibleAnswerValues().stream().filter(possibleAnswerValueDTO -> possibleAnswerValueDTO.getText().equals("méz")).findFirst().get();
        poResp1.setSelectedAnswerIds(new ArrayList<>());
        poResp1.getSelectedAnswerIds().add(méz.getId());


        possAnswers.add(poResp1);
        resp.setAnswers(possAnswers);
        AnswerFeedbackDTO answerFeedbackDTO = submitAnswer(resp, mockMvc, objectMapper);
        assertTrue(answerFeedbackDTO.isSuccessFullResult());
        assertEquals(AnswerEvaulationResult.falseAnswer, answerFeedbackDTO.getResult());
    }




}
