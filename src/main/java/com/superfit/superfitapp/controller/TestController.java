package com.superfit.superfitapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/teste")
    public String teste() {
        return "Acesso liberado com JWT";
    }
}
