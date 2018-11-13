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
}
