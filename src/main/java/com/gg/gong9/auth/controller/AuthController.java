package com.gg.gong9.auth.controller;

import com.gg.gong9.auth.controller.dto.*;
import com.gg.gong9.auth.service.AuthService;
import com.gg.gong9.user.controller.dto.JoinRequest;
import com.gg.gong9.user.controller.dto.LoginRequest;
import com.gg.gong9.user.controller.dto.LoginResponse;
import com.gg.gong9.user.controller.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody JoinRequest joinRequest) {

        UserResponse userId = authService.join(joinRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        LoginResponse loginResponse = authService.login(loginRequest);

        return ResponseEntity.ok(loginResponse);

    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    //이메일 본인인증 요청
    @PostMapping("/email/request")
    public ResponseEntity<String> requestEmail(@Valid @RequestBody EmailRequest request){
        authService.sendEmailCode(request);
        return ResponseEntity.ok("회원가입 인증번호가 발송되었습니다.");
    }

    //이메일 인증 코드 확인
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmailCode(@Valid @RequestBody EmailCheckRequest request){
        authService.verifyEmailCode(request);
        return ResponseEntity.ok("회원가입 인증이 완료되었습니다.");
    }

    //비밀번호 재설정 인증 요청
    @PostMapping("/password/reset/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request){
        authService.sendPasswordResetCode(request);
        return ResponseEntity.ok("비밀번호 재설정 인증번호가 발송되었습니다.");
    }

    //비밀번호 재설정 인증 코드 확인
    @PostMapping("/password/reset/verify")
    public ResponseEntity<String> verifyPasswordResetCode(@Valid @RequestBody PasswordResetCheckRequest request){
        authService.verifyPasswordResetCode(request);
        return ResponseEntity.ok("비밀번호 재설정 인증이 완료되었습니다.");
    }

    //비민번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request){
        authService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }
}
