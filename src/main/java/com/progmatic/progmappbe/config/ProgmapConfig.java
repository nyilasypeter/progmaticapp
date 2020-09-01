/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import java.util.ArrayList;
import java.util.List;
import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 *
 * @author peti
 */
@Configuration
public class ProgmapConfig {

    @Bean
    public DozerBeanMapper dozerMapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        List<String> mappingFileUrls = new ArrayList<>();
        mappingFileUrls.add("dozer-bean-mappings.xml");
        mapper.setMappingFiles(mappingFileUrls);
        return mapper;
    }

    @Bean
    public ITemplateResolver thymeleafStringTemplateResolver() {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafStringTemplateResolver());
        return templateEngine;
    }
}
