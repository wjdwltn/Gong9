package com.gg.gong9.user.entity;

import com.gg.gong9.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


    @Builder
    public User(String username, String email, String password, Address address, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
        this.userRole = userRole;
    }

}
