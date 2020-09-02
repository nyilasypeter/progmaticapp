/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 *
 * @author peti
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    CustomAuthenticationFailureHandler failureHandler;

    @Value("${progmatic.cors.allowed.origins}")
    private String allowedCorsOrigins;

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new ProgmaUserDetailsService();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .formLogin()
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                
        .and().exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
        //the default lazy csrf token makes it impossible to get the csrf token before a POST was made
        .and().csrf().
                csrfTokenRepository(new HttpSessionCsrfTokenRepository())
        .and().authorizeRequests()
                .antMatchers("/csrf", "/completeregistration").permitAll()
                .anyRequest().authenticated();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-CSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList(allowedCorsOrigins.split(";")));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT","PATCH","HEAD","OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
