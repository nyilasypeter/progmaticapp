package com.progmatic.progmappbe.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CookieSameSiteFilter implements Filter {

    public static final Logger LOGGER = LoggerFactory.getLogger(CookieSameSiteFilter.class);
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        LOGGER.trace("CookieSameSiteFilter doFilter called");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if(StringUtils.isNotBlank(response.getHeader("Set-Cookie"))){
            LOGGER.trace("SameSite=None added to cookie");
            response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=None");
        }
        chain.doFilter(request, response);

    }
}
