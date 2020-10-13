/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.config;

import com.progmatic.progmappbe.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author peti
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND = "loginFromBrowserDircetlyFromBackend";
    private static Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

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
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and()
                //for some reasion default logout page and default login page does not work with Http403ForbiddenEntryPoint
                .addFilter(defaultLoginPageGeneratingFilter())
                .addFilter(defaultLogoutPageGeneratingFilter())
                .csrf().
                csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                .and()
                .authorizeRequests()
                //connfigure actuator endpoints to be accessible only by admins
                .requestMatchers(org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint()).hasRole(Role.ROLE_ADMIN)
                //connfigure swagger endpoints to be accessible only by admins
                .antMatchers(
                        "/v3/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**").hasRole(Role.ROLE_ADMIN)
                .antMatchers(
                        "/csrf",
                        "/completeregistration").permitAll()
                .anyRequest().authenticated();
    }

    private DefaultLoginPageGeneratingFilter defaultLoginPageGeneratingFilter(){
        DefaultLoginPageGeneratingFilter defaultLoginPageGeneratingFilter = new DefaultLoginPageGeneratingFilter();
        defaultLoginPageGeneratingFilter.setLoginPageUrl("/login");
        defaultLoginPageGeneratingFilter.setFormLoginEnabled(true);
        defaultLoginPageGeneratingFilter.setAuthenticationUrl("/login");
        defaultLoginPageGeneratingFilter.setUsernameParameter("username");
        defaultLoginPageGeneratingFilter.setPasswordParameter("password");
        Function<HttpServletRequest, Map<String, String>> hiddenInputs = request -> {
            Map<String, String> ret = new HashMap<>();
            CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (token != null) {
                ret.put(token.getParameterName(), token.getToken());
            }
            ret.put(LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND, LOGIN_FROM_BROWSER_DIRCETLY_FROM_BACKEND);
            return ret;
        };
        defaultLoginPageGeneratingFilter.setResolveHiddenInputs(hiddenInputs);
        return defaultLoginPageGeneratingFilter;
    }

    private DefaultLogoutPageGeneratingFilter defaultLogoutPageGeneratingFilter(){
        DefaultLogoutPageGeneratingFilter defaultLoginPageGeneratingFilter = new DefaultLogoutPageGeneratingFilter();
        Function<HttpServletRequest, Map<String, String>> hiddenInputs = request -> {
            CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (token == null) {
                return Collections.emptyMap();
            }
            return Collections.singletonMap(token.getParameterName(), token.getToken());
        };
        defaultLoginPageGeneratingFilter.setResolveHiddenInputs(hiddenInputs);
        return defaultLoginPageGeneratingFilter;
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-CSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList(allowedCorsOrigins.split(";")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH", "HEAD", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    AuthenticationEntryPoint myAuthenticationEntryPoint(){
        AuthenticationEntryPoint ap = new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                LOGGER.debug("AuthenticationEntryPoint commence");
                LOGGER.debug(request.getRequestURI());
            }
        };
        return ap;
    }


}
