package com.progmatic.progmappbe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.user.RoleDTO;
import com.progmatic.progmappbe.dtos.user.UserDTO;
import com.progmatic.progmappbe.dtos.user.UserModificationDTO;
import com.progmatic.progmappbe.dtos.user.UserSearchResponseDTO;
import com.progmatic.progmappbe.entities.BaseEntity;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.Charset;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OfficeAdminTestBase {

    protected void loginAs(String userName, String password, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception{
        mockMvc.perform(
                SecurityMockMvcRequestBuilders.formLogin().user(userName).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
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

    protected void createUser(String userName, MockMvc mockMvc, ObjectMapper objectMapper, String... roles ) throws Exception{
        UserDTO user = new UserDTO();
        user.setId(userName);
        user.setLoginName(userName);
        user.setPassword(userName);
        user.setName(userName);
        user.setEmailAddress(userName + "@progmaticmail.com");
        for (String role : roles) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setName(role);
            user.getRoles().add(roleDTO);
        }

        mockMvc.perform(
                post("/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andExpect(jsonPath("$.idOfCreatedEntity", Matchers.is(userName)));
    }


    protected BasicResult modifyStudent(UserModificationDTO modDto, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                put("/student")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        BasicResult baseEntity = objectMapper.readValue(json, BasicResult.class);
        return baseEntity;
    }

    protected BasicResult modifyUser(UserModificationDTO modDto, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                put("/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successFullResult", Matchers.is(true)))
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
        BasicResult baseEntity = objectMapper.readValue(json, BasicResult.class);
        return baseEntity;
    }

    protected UserSearchResponseDTO findUserByLoginName(String loginName, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/user")
                        .param("loginName", loginName))
                .andExpect(status().isOk())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
        List<UserSearchResponseDTO> resp = objectMapper.readValue(json, new TypeReference<List<UserSearchResponseDTO>>() {});
        Assert.assertEquals(1, resp.size());
        return resp.get(0);

    }


}
