package com.superfit.superfitapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/admin")

public class TestAdminController {

    @GetMapping("/teste")
    public String teste() {
        return "ADMIN OK";
    }
}