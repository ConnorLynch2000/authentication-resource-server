package org.rajman.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;

@RestController
@RequestMapping("/health")
public class HealthController {

    EntityManager entityManager;

    @GetMapping(produces = "application/json")
    public Boolean health(){
        return (Boolean)entityManager.createNativeQuery("SELECT true")
                .getSingleResult();
    }
}
