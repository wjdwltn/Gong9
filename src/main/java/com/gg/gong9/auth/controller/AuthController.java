package com.gg.gong9.auth.controller;

import com.gg.gong9.auth.controller.dto.*;
import com.gg.gong9.auth.service.AuthService;
import com.gg.gong9.global.security.cookie.CookieUtil;
import com.gg.gong9.global.security.jwt.JwtTokenProvider;
import com.gg.gong9.user.controller.dto.*;
import com.gg.gong9.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    //구매자 회원가입
    @PostMapping("/signup/buyer")
    public ResponseEntity<UserIdResponse> signupBuyer(@Valid @RequestBody BuyerJoinRequest joinRequest) {

        UserIdResponse userId = authService.buyerJoin(joinRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }

    //판매자 회원가입
    @PostMapping("/signup/seller")
    public ResponseEntity<UserIdResponse> signupSeller(@Valid @RequestBody SellerJoinRequest joinRequest) {

        UserIdResponse userId = authService.sellerJoin(joinRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response)
    {
        LoginResponse loginResponse = authService.login(loginRequest,response);

        return ResponseEntity.ok(loginResponse);

    }

    //카카오 로그인
    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse){
        LoginResponse loginResponse = authService.kakaoLogin(accessCode,httpServletResponse);
        return ResponseEntity.ok(loginResponse);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getRefreshTokenFromCookies(request);

        if(refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = jwtTokenProvider.getUserFromToken(refreshToken);

        authService.logout(response,user.getEmail());
        return ResponseEntity.ok(new AuthResponse("로그아웃이 완료되었습니다."));
    }

    //토큰 재발행
    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getRefreshTokenFromCookies(request);

        if(refreshToken == null) {
            // 쿠키에 토큰이 없으면 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TokenReissueResponse reissueResponse = authService.reissueToken(refreshToken,response);
        return ResponseEntity.ok(reissueResponse);
    }

    //이메일 본인인증 요청
    @PostMapping("/email/request")
    public ResponseEntity<AuthResponse> requestEmail(@Valid @RequestBody EmailRequest request){
        authService.sendEmailCode(request);
        return ResponseEntity.ok(new AuthResponse("회원가입 인증번호가 발송되었습니다."));
    }

    //이메일 인증 코드 확인
    @PostMapping("/email/verify")
    public ResponseEntity<AuthResponse> verifyEmailCode(@Valid @RequestBody EmailCheckRequest request){
        authService.verifyEmailCode(request);
        return ResponseEntity.ok(new AuthResponse("회원가입 인증이 완료되었습니다."));
    }

    //비밀번호 재설정 인증 요청
    @PostMapping("/password/reset/request")
    public ResponseEntity<AuthResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request){
        authService.sendPasswordResetCode(request);
        return ResponseEntity.ok(new AuthResponse("비밀번호 재설정 인증번호가 발송되었습니다."));
    }

    //비밀번호 재설정 인증 코드 확인
    @PostMapping("/password/reset/verify")
    public ResponseEntity<AuthResponse> verifyPasswordResetCode(@Valid @RequestBody PasswordResetCheckRequest request){
        authService.verifyPasswordResetCode(request);
        return ResponseEntity.ok(new AuthResponse("비밀번호 재설정 인증이 완료되었습니다."));
    }

    //비민번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request){
        authService.resetPassword(request);
        return ResponseEntity.ok(new AuthResponse("비밀번호가 재설정되었습니다."));
    }
}
