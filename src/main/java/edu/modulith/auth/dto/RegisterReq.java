package edu.modulith.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterReq(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank @Size(min=6) String password
) {}