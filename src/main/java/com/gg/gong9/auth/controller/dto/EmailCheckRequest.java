package com.gg.gong9.auth.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCheckRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String code
) {
}
