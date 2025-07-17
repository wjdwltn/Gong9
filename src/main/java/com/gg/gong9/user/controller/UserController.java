package com.gg.gong9.user.controller;

import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.user.controller.dto.*;
import com.gg.gong9.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> getUser(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return ResponseEntity.ok(userService.getUser(userId));
    }

    //회원 정보 수정
    @PatchMapping
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal CustomUserDetails userPrincipal,
                                             @Valid @RequestBody UpdateUserRequest updateUserRequest)
    {
        Long userId = userPrincipal.getUser().getId();
        userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(new UserResponse("회원 정보 수정이 완료되었습니다."));
    }

    //비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<UserResponse> changePassword(@AuthenticationPrincipal CustomUserDetails userPrincipal,
                                                 @Valid @RequestBody ChangePasswordRequest changePasswordRequest)
    {
        Long userId = userPrincipal.getUser().getId();
        userService.updatePassword(userId, changePasswordRequest);
        return ResponseEntity.ok(new UserResponse("비밀번호 변경이 완료되었습니다."));
    }

    // 회원 탈퇴 (Soft Delete)
    @DeleteMapping
    public ResponseEntity<UserResponse> deleteUser(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.ok(new UserResponse("탈퇴가 완료되었습니다."));
    }
}
