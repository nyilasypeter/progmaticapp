package com.progmatic.progmappbe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.user.RoleDTO;
import com.progmatic.progmappbe.dtos.user.UserSearchResponseDTO;
import com.progmatic.progmappbe.entities.Role;
import com.progmatic.progmappbe.helpers.UserModificationDTOBuilder;
import com.progmatic.progmappbe.dtos.user.UserModificationDTO;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OfficeAdminTest extends OfficeAdminTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("admin")
    @Order(10)
    void adminShuldBeAbleToCreateStudent() throws Exception {
        createStudent("diak_01", mockMvc, objectMapper);
        loginAs("diak_01", "diak_01", mockMvc, objectMapper);
    }

    @Test
    @WithUserDetails("admin")
    @Order(1)
    void adminShuldBeAbleToCreateOfficeAdmin() throws Exception {
        createUser("irodai_01", mockMvc, objectMapper, Role.ROLE_OFFICE);
        loginAs("irodai_01", "irodai_01", mockMvc, objectMapper);
    }

    @Test
    @WithUserDetails("admin")
    @Order(1)
    void adminShuldBeAbleToCreateAdmin() throws Exception {
        createUser("admin_01", mockMvc, objectMapper, Role.ROLE_ADMIN);
        loginAs("admin_01", "admin_01", mockMvc, objectMapper);
    }

    @Test
    @WithUserDetails("admin")
    @Order(20)
    void adminShuldBeAbleToModifyStudent() throws Exception {
        System.out.println("adminShuldBeAbleToModifyStudent");
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder().setLoginName("diak_01").setPassword("titkos").build();
        modifyStudent(userMod, mockMvc, objectMapper);
        loginAs("diak_01", "titkos", mockMvc, objectMapper);
    }

    @Test
    @WithUserDetails("admin")
    @Order(21)
    void adminShuldBeAbleToModifyStudent2() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder().setLoginName("diak_01").setName("Pityuka").setEmailAddress("pityuka@gmail.com").build();
        modifyStudent(userMod, mockMvc, objectMapper);
        UserSearchResponseDTO diak_01 = findUserByLoginName("diak_01", mockMvc, objectMapper);
        Assert.assertEquals("Pityuka", diak_01.getName());
        Assert.assertEquals("pityuka@gmail.com", diak_01.getEmailAddress());
    }

    @Test
    @WithUserDetails("admin")
    @Order(20)
    void adminShuldBeAbleToModifyOfficeAdmin() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder()
                .setLoginName("irodai_01")
                .setName("Irodista Pista")
                .setEmailAddress("irodistapista@progmatic.hu")
                .build();
        modifyUser(userMod, mockMvc, objectMapper);
        UserSearchResponseDTO irodai_01 = findUserByLoginName("irodai_01", mockMvc, objectMapper);
        Assert.assertEquals("Irodista Pista", irodai_01.getName());
        Assert.assertEquals("irodistapista@progmatic.hu", irodai_01.getEmailAddress());
    }

    @Test
    @WithUserDetails("admin")
    @Order(22)
    void adminShuldBeAbleToAddRolesToOfficeAdmin() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder()
                .setLoginName("irodai_01")
                .addRole(Role.ROLE_ADMIN)
                .addRole(Role.ROLE_TEACHER)
                .addRole(Role.ROLE_OFFICE)
                .build();
        modifyUser(userMod, mockMvc, objectMapper);
        UserSearchResponseDTO irodai_01 = findUserByLoginName("irodai_01", mockMvc, objectMapper);
        Assert.assertEquals("Irodista Pista", irodai_01.getName());
        Assert.assertEquals("irodistapista@progmatic.hu", irodai_01.getEmailAddress());
        List<RoleDTO> roles = irodai_01.getRoles();
        Assert.assertEquals(3, roles.size());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_ADMIN)).findFirst().isPresent());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_TEACHER)).findFirst().isPresent());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_OFFICE)).findFirst().isPresent());
    }

    @Test
    @WithUserDetails("admin")
    @Order(23)
    void adminShuldBeAbleToDeleteRolesFromOfficeAdmin() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder()
                .setLoginName("irodai_01")
                .addRole(Role.ROLE_ADMIN)
                .addRole(Role.ROLE_OFFICE)
                .build();
        modifyUser(userMod, mockMvc, objectMapper);
        UserSearchResponseDTO irodai_01 = findUserByLoginName("irodai_01", mockMvc, objectMapper);
        Assert.assertEquals("Irodista Pista", irodai_01.getName());
        Assert.assertEquals("irodistapista@progmatic.hu", irodai_01.getEmailAddress());
        List<RoleDTO> roles = irodai_01.getRoles();
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_ADMIN)).findFirst().isPresent());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_OFFICE)).findFirst().isPresent());
        Assert.assertFalse(roles.stream().filter(role -> role.getName().equals(Role.ROLE_TEACHER)).findFirst().isPresent());
    }


    @Test
    @WithUserDetails("admin")
    @Order(24)
    void adminShuldNotBeAbleToRemoveAdminRight() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder()
                .setLoginName("irodai_01")
                .addRole(Role.ROLE_OFFICE)
                .build();
        BasicResult modificationResult = modifyUser(userMod, mockMvc, objectMapper);
        Assert.assertEquals(1, modificationResult.getNotes().size());
        UserSearchResponseDTO irodai_01 = findUserByLoginName("irodai_01", mockMvc, objectMapper);
        Assert.assertEquals("Irodista Pista", irodai_01.getName());
        Assert.assertEquals("irodistapista@progmatic.hu", irodai_01.getEmailAddress());
        List<RoleDTO> roles = irodai_01.getRoles();
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_ADMIN)).findFirst().isPresent());
        Assert.assertTrue(roles.stream().filter(role -> role.getName().equals(Role.ROLE_OFFICE)).findFirst().isPresent());
        Assert.assertFalse(roles.stream().filter(role -> role.getName().equals(Role.ROLE_TEACHER)).findFirst().isPresent());
    }


    @Test
    @WithUserDetails("officeUser")
    @Order(22)
    void officeuserShuldNotBeAbleToAddRolesToStudent() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder()
                .setLoginName("diak_01")
                .addRole(Role.ROLE_OFFICE)
                .build();
        BasicResult basicResult = modifyStudent(userMod, mockMvc, objectMapper);
        Assert.assertEquals(1, basicResult.getNotes().size());
    }

    @Test
    @WithUserDetails("student")
    @Order(22)
    void studentShuldNotBeAbleToModifyOtherStudent() throws Exception {
        UserModificationDTO userMod = UserModificationDTOBuilder.newBuilder()
                .setLoginName("diak_01")
                .addRole(Role.ROLE_OFFICE)
                .build();
        mockMvc.perform(
                put("/student")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMod)))
                .andExpect(status().isForbidden());
    }


}
