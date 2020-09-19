package com.progmatic.progmappbe;

import org.hamcrest.Matchers;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCsf()  throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        String csrfToken = mvcResult.getResponse().getContentAsString();
        assertNotNull(csrfToken);
    }

    @Test
    void logiWithout_csrf() throws Exception {
        mockMvc.perform(
                post("/login")
                        .contentType("multipart/form-data")
                        .content("username:admin\n" +
                                "password:admin"))
                .andExpect(status().is(403));
    }

    /*@Test
    void loginAsAdmin() throws Exception {
        mockMvc.perform(
                post("/login")
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("admin")));
    }*/

    @Test
    void loginAsAdmin() throws Exception {
       mockMvc.perform(
               SecurityMockMvcRequestBuilders.formLogin().user("admin").password("admin"))
               .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    void loginWithInvalidCredentials() throws Exception {
        mockMvc.perform(
                SecurityMockMvcRequestBuilders.formLogin().user("admin").password("aaa"))
                .andExpect(status().is(401));

        mockMvc.perform(
                SecurityMockMvcRequestBuilders.formLogin().user("aaa").password("aaa"))
                .andExpect(status().is(401));
    }
}
