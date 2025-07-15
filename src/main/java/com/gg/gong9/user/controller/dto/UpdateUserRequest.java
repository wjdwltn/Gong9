package com.gg.gong9.user.controller.dto;

import com.gg.gong9.user.entity.Address;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateUserRequest(

        @NotNull
        @Length(min = 3, max = 20, message = "3~20 글자로 작성해주세요.")
        String username,

        @NotNull
        String postcode,

        @NotNull
        String detail
) {
        public Address toAddress() {
                return new Address(postcode, detail);
        }
}
