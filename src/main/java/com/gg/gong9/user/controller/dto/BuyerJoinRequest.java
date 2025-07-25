package com.gg.gong9.user.controller.dto;
import com.gg.gong9.user.entity.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record BuyerJoinRequest(

        @NotNull
        @Email(message = "이메일 형식으로 입력해주세요.")
        String email,

        @NotNull
        @Length(min = 4, message = "4 글자 이상으로 작성해주세요.")
        String password,

        @NotNull
        @Length(min = 3, max = 20, message = "3~20 글자로 작성해주세요.")
        String username,

        @NotNull
        @Pattern(regexp = "^\\d{10,11}$", message = "'-'는 제외하여 작성해주세요.")
        String phoneNumber,

        @NotNull
        String postcode,

        @NotNull
        String detail

){
        public Address toAddress() {
                return new Address(postcode, detail);
        }
}