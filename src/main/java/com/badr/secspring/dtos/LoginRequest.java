package com.badr.secspring.dtos;

public record LoginRequest(
    String email,
    String password
) { }
