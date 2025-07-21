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

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder
    public User(String username, String email, String password, Address address, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
        this.userRole = userRole;
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
        return User.builder()
                .username(nickname)
                .email(email)
                .password(null)
                .address(null)
                .userRole(UserRole.USER)
                .build();
    }

}
