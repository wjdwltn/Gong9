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
    public ResponseEntity<?> getUser(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return ResponseEntity.ok(userService.getUser(userId));
    }

    //회원 정보 수정
    @PatchMapping
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal CustomUserDetails userPrincipal,
                                             @Valid @RequestBody UpdateUserRequest updateUserRequest)
    {
        Long userId = userPrincipal.getUser().getId();
        userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok("회원 정보 수정 성공");
    }

    //비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal CustomUserDetails userPrincipal,
                                                 @Valid @RequestBody ChangePasswordRequest changePasswordRequest)
    {
        Long userId = userPrincipal.getUser().getId();
        userService.updatePassword(userId, changePasswordRequest);
        return ResponseEntity.ok("회원 비밀번호 변경 성공");
    }

    // 회원 탈퇴 (Soft Delete)
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }
}
