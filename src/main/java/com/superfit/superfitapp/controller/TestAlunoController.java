package com.superfit.superfitapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/aluno")

public class TestAlunoController {

    @GetMapping("/teste")
    public String teste() {
        return "Aluno OK";
    }
}
