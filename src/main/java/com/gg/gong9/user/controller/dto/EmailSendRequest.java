package com.gg.gong9.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailSendRequest(

        @NotBlank
        @Email
        String email
) {
}
