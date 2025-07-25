package com.gg.gong9.user.controller.dto;
import com.gg.gong9.user.entity.Address;
import com.gg.gong9.user.entity.BankAccount;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record SellerJoinRequest(

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
        String bankName,

        @NotNull
        String accountNumber

){
        public BankAccount toBankAccount() {
                return new BankAccount(bankName, accountNumber);
        }
}