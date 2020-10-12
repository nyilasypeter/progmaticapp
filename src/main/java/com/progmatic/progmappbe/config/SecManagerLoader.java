package com.progmatic.progmappbe.config;

import com.progmatic.progmappbe.helpers.sourceevaluator.MyClassloader;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.security.Policy;
import java.util.HashMap;

@Component
public class SecManagerLoader implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        MyClassloader mc = new MyClassloader();
        mc.getName();
        MyPolicy myPolicy = new MyPolicy();
        Policy.setPolicy(myPolicy);
        SecurityManager securityManager = new SecurityManager();
        System.setSecurityManager(securityManager);
    }
}
