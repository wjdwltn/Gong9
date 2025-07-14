package com.gg.gong9.user.controller.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
