package com.badr.secspring.services;

import com.badr.secspring.dtos.AuthResponse;
import com.badr.secspring.dtos.LoginRequest;
import com.badr.secspring.dtos.RegisterRequest;
import com.badr.secspring.dtos.UserDTO;
import com.badr.secspring.entites.AppUser;
import com.badr.secspring.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email deja utilise");
        }
        AppUser user = new AppUser();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        userRepository.save(user);
        return new UserDTO(user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        // leve BadCredentialsException si l'email ou le mot de passe est faux
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponse(user.getName(), user.getEmail(), token);
    }

    public UserDTO findByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        return new UserDTO(user.getName(), user.getEmail());
    }
}
