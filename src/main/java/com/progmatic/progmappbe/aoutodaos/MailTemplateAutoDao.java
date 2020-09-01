package com.progmatic.progmappbe.aoutodaos;

import com.progmatic.progmappbe.entities.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTemplateAutoDao extends JpaRepository<MailTemplate, String> {
}
