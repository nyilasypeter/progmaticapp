package com.progmatic.progmappbe.services;

import com.progmatic.progmappbe.entities.Constant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ConstantService {

    public static final String KEY_ETERNALQUIZ_TARGET_PERCENTAGE = "KEY_ETERNALQUIZ_TARGET_PERCENTAGE";

    @PersistenceContext
    EntityManager em;

    public Constant getConstantByKey(String key){
        return em.find(Constant.class, key);
    }

    public String getConstantValueByKey(String key){
        Constant c = em.find(Constant.class, key);
        if(c == null){
            return null;
        }
        return c.getValue();
    }

    public Integer getConstantValueAsIntegerByKey(String key){
        return Integer.valueOf(getConstantValueByKey(key));
    }

    @Transactional
    public void writeConstant(String key, String value){
        Constant c = new Constant();
        c.setId(key);
        c.setValue(value);
        em.persist(c);
    }
}
