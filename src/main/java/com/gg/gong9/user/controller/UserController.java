package com.gg.gong9.user.controller;

import com.gg.gong9.auth.controller.dto.*;
import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.user.controller.dto.*;
import com.gg.gong9.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody JoinRequest joinRequest) {

        UserResponse userId = userService.join(joinRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        LoginResponse loginResponse = userService.login(loginRequest);

        return ResponseEntity.ok(loginResponse);

    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

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

    //이메일 본인인증 요청
    @PostMapping("/email/request")
    public ResponseEntity<String> requestEmail(@Valid @RequestBody EmailRequest request){
        userService.sendEmailCode(request);
        return ResponseEntity.ok("회원가입 인증번호가 발송되었습니다.");
    }

    //이메일 인증 코드 확인
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmailCode(@Valid @RequestBody EmailCheckRequest request){
        userService.verifyEmailCode(request);
        return ResponseEntity.ok("회원가입 인증이 완료되었습니다.");
    }

    //비밀번호 재설정 인증 요청
    @PostMapping("/password/reset/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request){
        userService.sendPasswordResetCode(request);
        return ResponseEntity.ok("비밀번호 재설정 인증번호가 발송되었습니다.");
    }

    //비밀번호 재설정 인증 코드 확인
    @PostMapping("/password/reset/verify")
    public ResponseEntity<String> verifyPasswordResetCode(@Valid @RequestBody PasswordResetCheckRequest request){
        userService.verifyPasswordResetCode(request);
        return ResponseEntity.ok("비밀번호 재설정 인증이 완료되었습니다.");
    }

    //비민번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request){
        userService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }

}
