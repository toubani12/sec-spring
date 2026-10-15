package com.badr.secspring.dtos;

public record AuthResponse(
    String name,
    String email,
    String token
) { }
