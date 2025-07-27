package com.gg.gong9.user.entity;

import com.gg.gong9.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String email;

    private String password;

    private String phoneNumber;

    @Embedded
    private Address address; // 구매자만 사용

    @Embedded
    private BankAccount bankAccount; // 판매자만 사용

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;


    public User(String username, String email, String password, String phoneNumber, Address address, BankAccount bankAccount, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bankAccount = bankAccount;
        this.userRole = userRole;
    }

    public static User createBuyer(String username, String email, String password, String phoneNumber, Address address) {
        return new User(
                username,
                email,
                password,
                phoneNumber,
                address,
                null,
                UserRole.USER);
    }

    // 판매자 회원 생성 메서드
    public static User createSeller(String username, String email, String password, String phoneNumber, BankAccount bankAccount) {
        return new User(
                username,
                email,
                password,
                phoneNumber,
                null,
                bankAccount,
                UserRole.ADMIN);
    }

    public void updateUser(String username, Address address) {
        this.username = username;
        this.address = address;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void softDelete(){
        this.isDeleted = true;
    }

    //소셜 로그인 생성
    public static User createSocialLoginUser(String nickname, String email) {
        return new User(nickname, email,null,null, null,null, UserRole.USER);
    }

}
