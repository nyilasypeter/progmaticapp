package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.entities.Privilige;
import com.progmatic.progmappbe.entities.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminpageController {

    @GetMapping("/adminpage")
    @PreAuthorize("hasRole('" + Role.ROLE_ADMIN + "')")
    public ResponseEntity<String> adminPage(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html");

        return new ResponseEntity<>(
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<title>Administration link</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "<h1>Useful links</h1>\n" +
                        "<a href=\"/actuator/health\" target=\"_blank\">Actuator health</a>\n" +
                        "<br>\n" +
                        "<a href=\"/actuator/info\" target=\"_blank\">Actuator info</a>\n" +
                        "<br>\n" +
                        "<a href=\"/swagger-ui.html\" target=\"_blank\">Swagger OPEN API html documentation</a>\n" +
                        "<br>\n" +
                        "<a href=\"/v3/api-docs/\" target=\"_blank\">Swagger OPEN API json</a>\n" +
                        "<br>\n" +
                        "<a href=\"/logout\">Log out</a>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>", headers, HttpStatus.OK);
    }


}

