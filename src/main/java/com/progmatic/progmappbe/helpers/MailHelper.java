package com.progmatic.progmappbe.helpers;

import com.progmatic.progmappbe.entities.MailTemplate;
import com.progmatic.progmappbe.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

@Component
public class MailHelper {

    public static final String RECIPIENT_KEY = "recipient";
    public static final String TOKEN_KEY = "registrationLink";

    public static final String MAIL_TEMPLATE_STUDENT_REGISTRATION = "MAIL_TEMPLATE_STUDENT_REGISTRATION";
    public static final String MAIL_TEMPLATE_USER_REGISTRATION = "MAIL_TEMPLATE_USER_REGISTRATION";

    @PersistenceContext
    EntityManager em;

    private JavaMailSender mailSender;

    private String fromAddress;

    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    public MailHelper(JavaMailSender mailSender,
                      @Value("${progmatic.admin.mail.from}") String fromAddress,
                      SpringTemplateEngine thymeleafTemplateEngine) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    public void sendMailByTemplate(String templateId, String to, Map<String, Object> templateModel){
        try{
            MailTemplate mailTemplate = em.find(MailTemplate.class, templateId);
            if(mailTemplate == null){
                throw new RuntimeException("Mail template with id does not exist. Id: " + templateId);
            }
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(fromAddress);
            message.setTo(to);
            if(StringUtils.isNotBlank(mailTemplate.getCc())){
                message.setCc(mailTemplate.getCc().split(";"));
            }
            message.setSubject(mailTemplate.getSubject());

            String body = thProcess(mailTemplate.getBody(), templateModel, thymeleafTemplateEngine);
            message.setText(body, mailTemplate.getHtml());
            mailSender.send(mimeMessage);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static String thProcess(String text2process, Map<String, Object> templateModel, SpringTemplateEngine engine){
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        return engine.process(text2process, thymeleafContext);
    }


}
