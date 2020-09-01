package com.progmatic.progmappbe.helpers;

import com.progmatic.progmappbe.config.CustomAuthenticationFailureHandler;
import com.progmatic.progmappbe.config.CustomAuthenticationSuccessHandler;
import com.progmatic.progmappbe.config.ProgmapConfig;
import com.progmatic.progmappbe.controllers.OfficeAdminController;
import com.progmatic.progmappbe.controllers.RegistrationController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {OfficeAdminController.class, RegistrationController.class})
@ContextConfiguration(classes = {
        CustomAuthenticationFailureHandler.class,
        CustomAuthenticationSuccessHandler.class,
        ProgmapConfig.class})
public class MailHelperTest {

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Test
    public void testThymeLeafHTML(){
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("greeting", "szia");
        String body = MailHelper.thProcess("<p th:text=\"${greeting}\"></p>", templateModel, thymeleafTemplateEngine);
        System.out.println(body);
        assertEquals("<p>szia</p>", body);
    }

    @Test
    public void testThymeLeafText(){
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("greeting", "Szia");
        String body = MailHelper.thProcess("[(${greeting})] Te!", templateModel, thymeleafTemplateEngine);
        System.out.println(body);
        assertEquals("Szia Te!", body);
    }

}