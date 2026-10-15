package com.badr.secspring.controllers;

import com.badr.secspring.dtos.AuthResponse;
import com.badr.secspring.dtos.LoginRequest;
import com.badr.secspring.dtos.RegisterRequest;
import com.badr.secspring.dtos.UserDTO;
import com.badr.secspring.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
