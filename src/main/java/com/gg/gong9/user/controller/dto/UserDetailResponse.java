package com.gg.gong9.user.controller.dto;

import com.gg.gong9.user.entity.Address;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;

public record UserDetailResponse(
        Long id,
        String username,
        String email,
        Address address,
        UserRole userRole
) {
    public static UserDetailResponse of(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAddress(),
                user.getUserRole()
        );
    }
}
