package com.gg.gong9.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(

        @NotNull
        @Email(message = "이메일 형식으로 입력해주세요.")
        String email,

        @NotNull
        @Length(min = 10, message = "10 글자 이상으로 작성해주세요.")
        String password
) {
}
