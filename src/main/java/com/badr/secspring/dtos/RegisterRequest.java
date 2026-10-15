package com.badr.secspring.dtos;

public record RegisterRequest(
     String name,
     String email,
     String password
) { }
