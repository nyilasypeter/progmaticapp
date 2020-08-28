package com.progmatic.progmappbe.aoutodaos;

import com.progmatic.progmappbe.entities.Constant;
import com.progmatic.progmappbe.entities.Privilige;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstantAutoDao extends JpaRepository<Constant, String> {
}
