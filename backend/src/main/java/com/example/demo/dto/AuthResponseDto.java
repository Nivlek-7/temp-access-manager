package com.example.demo.dto;

import com.example.demo.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponseDto(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...") String token,
        @Schema(example = "USER") Role role) {
}
