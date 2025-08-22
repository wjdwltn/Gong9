package com.gg.gong9.minibuy.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MiniBuyShareRequestDto(
        @NotBlank(message = "공유 링크는 필수입니다.")
        String link
) {
}