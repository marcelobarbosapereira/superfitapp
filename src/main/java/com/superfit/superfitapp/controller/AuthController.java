package com.superfit.superfitapp.controller;

import com.superfit.superfitapp.dto.LoginRequest;
import com.superfit.superfitapp.dto.LoginResponse;
import com.superfit.superfitapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(new LoginResponse(token));
    }

}

