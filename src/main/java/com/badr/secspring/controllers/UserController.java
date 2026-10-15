package com.badr.secspring.controllers;

import com.badr.secspring.dtos.UserDTO;
import com.badr.secspring.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Bienvenue dans l'API securisee";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication) {
        return "Utilisateur connecte : " + authentication.getName();
    }

    @GetMapping("/me")
    public UserDTO me(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }
}
