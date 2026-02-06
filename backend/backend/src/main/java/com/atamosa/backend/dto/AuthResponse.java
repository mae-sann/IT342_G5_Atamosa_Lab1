package com.atamosa.backend.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
}