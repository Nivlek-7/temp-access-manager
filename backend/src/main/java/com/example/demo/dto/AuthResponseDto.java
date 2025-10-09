package com.example.demo.dto;

import com.example.demo.model.Role;

public record AuthResponseDto (String token, Role role) {
}
