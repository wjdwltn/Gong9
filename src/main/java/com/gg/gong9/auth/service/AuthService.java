package com.gg.gong9.auth.service;

import com.gg.gong9.auth.controller.dto.*;
import com.gg.gong9.auth.repository.RefreshTokenRepository;
import com.gg.gong9.auth.repository.VerificationCodeRepository;
import com.gg.gong9.global.exception.exceptions.auth.AuthException;
import com.gg.gong9.global.exception.exceptions.auth.AuthExceptionMessage;
import com.gg.gong9.global.exception.exceptions.user.UserException;
import com.gg.gong9.global.exception.exceptions.user.UserExceptionMessage;
import com.gg.gong9.global.security.cookie.CookieUtil;
import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.global.security.jwt.CustomUserDetailsService;
import com.gg.gong9.global.security.jwt.JwtTokenProvider;
import com.gg.gong9.mail.service.MailService;
import com.gg.gong9.user.controller.dto.JoinRequest;
import com.gg.gong9.user.controller.dto.LoginRequest;
import com.gg.gong9.user.controller.dto.LoginResponse;
import com.gg.gong9.user.controller.dto.UserIdResponse;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import com.gg.gong9.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;

    private static final long CODE_TTL = 300; //5분
    private static final String EMAIL_CODE_PREFIX = "emailCode:";
    private static final String PASSWORD_RESET_CODE_PREFIX = "passwordResetCode:";
    private final CustomUserDetailsService customUserDetailsService;

    //회원가입
    public UserIdResponse join(JoinRequest joinRequest){
        checkEmailDuplication(joinRequest.email());

        User user = User.builder()
                .username(joinRequest.username())
                .email(joinRequest.email())
                .password(bCryptPasswordEncoder.encode(joinRequest.password()))
                .address(joinRequest.toAddress())
                .userRole(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        return new UserIdResponse(savedUser.getId());
    }

    //로그인
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response){
        User user = findByEmailOrThrow(loginRequest.email());

        validatePasswordOrThrow(loginRequest.password(), user.getPassword());

        //JWT 토근 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        //Redis에 refreshToken 저장 REFRESH_TOKEN_PREFIX
        refreshTokenRepository.saveRefreshToken(user.getEmail(),refreshToken);

        //쿠키에 refreshToken 저장
        CookieUtil.createRefreshTokenCookie(refreshToken,response);

        return new LoginResponse(user.getId(), accessToken,refreshToken);
    }

    public void logout(HttpServletResponse response, String email) {
        refreshTokenRepository.deleteRefreshToken(email);
        CookieUtil.deleteRefreshTokenFromCookies(response);
    }

    //토큰 재발행
    public TokenReissueResponse reissueToken(String refreshToken, HttpServletResponse response){

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(AuthExceptionMessage.INVALID_REFRESH_TOKEN);
        }

        User user = jwtTokenProvider.getUserFromToken(refreshToken);

        String savedRefreshToken = refreshTokenRepository.getRefreshToken(user.getEmail())
                .orElseThrow(()->new AuthException(AuthExceptionMessage.INVALID_REFRESH_TOKEN));

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthExceptionMessage.REFRESH_TOKEN_MISMATCH);
        }

        //새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

        // Redis에 새 토큰 저장
        refreshTokenRepository.saveRefreshToken(user.getEmail(), newRefreshToken);

        //쿠키에서 기존 토큰 삭제 & 새로운 토큰 저장
        CookieUtil.deleteRefreshTokenFromCookies(response);
        CookieUtil.createRefreshTokenCookie(newRefreshToken,response);

        return new TokenReissueResponse(newAccessToken,newRefreshToken);
    }

    //이메일 인증 번호 요청
    public void sendEmailCode(EmailRequest request){
        String verificationCode = createVerificationCode();

        saveVerificationCode(EMAIL_CODE_PREFIX, request.email(), verificationCode, CODE_TTL);

        String title = "이메일 인증 코드";
        String content = "아래 인증 코드를 입력하여 이메일 인증을 완료하세요.\n\n인증 코드: {code}";

        sendEmailWithCode(request.email(), title, content, verificationCode);
    }

    //이메일 인증 코드 확인
    public void verifyEmailCode(EmailCheckRequest request){
        verifyCode(EMAIL_CODE_PREFIX, request.email(), request.code());
    }

    //비밀번호 재설정 요청
    public void sendPasswordResetCode(PasswordResetRequest request){
        if(!userRepository.existsByEmail(request.email())){
            throw new AuthException(AuthExceptionMessage.EMAIL_NOT_FOUND);
        }

        String resetCode = createVerificationCode();

        saveVerificationCode(PASSWORD_RESET_CODE_PREFIX, request.email(), resetCode, CODE_TTL);

        String title = "비밀번호 재설정 코드";
        String content = "아래 인증 코드를 입력하여 비밀번호를 재설정하세요.\n\n재설정 코드: {code}";

        sendEmailWithCode(request.email(), title, content, resetCode);

    }

    //비밀번호 재설정 인증 코드 확인
    public void verifyPasswordResetCode(PasswordResetCheckRequest request){
        verifyCode(PASSWORD_RESET_CODE_PREFIX, request.email(), request.code());
    }

    //비밀번호 재설정
    @Transactional
    public void resetPassword(PasswordResetConfirmRequest request){
        User user = findByEmailOrThrow(request.email());

        user.updatePassword(bCryptPasswordEncoder.encode(request.newPassword()));
    }

    //인증번호 생성
    private String createVerificationCode(){
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(chars.length());
                sb.append(chars.charAt(index));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Secure random algorithm not available", e);
        }
    }

    //인증코드 Redis에 저장
    private void saveVerificationCode(String keyPrefix, String email, String code, long ttl) {
        String key = keyPrefix + email;
        verificationCodeRepository.saveCode(key, code, ttl);
    }

    private void sendEmailWithCode(String email, String title, String content, String code) {
        String body = content.replace("{code}", code);
        mailService.sendEmail(email, title, body);
    }

    public void verifyCode(String keyPrefix, String email, String code){
        String key = keyPrefix + email;
        String storedCode = verificationCodeRepository.getCode(key)
                .orElseThrow(()->new AuthException(AuthExceptionMessage.INVALID_VERIFICATION_CODE));

        if (!storedCode.equals(code)) {
            throw new AuthException(AuthExceptionMessage.VERIFICATION_CODE_NOT_MATCHED);
        }

        verificationCodeRepository.deleteCode(key);
    }

    //검증

    private User findByEmailOrThrow(String email){
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(()-> new UserException(UserExceptionMessage.INVALID_EMAIL));
    }

    public void validatePasswordOrThrow(String password, String encodedPassword){
        if (!bCryptPasswordEncoder.matches(password, encodedPassword)) {
            throw new UserException(UserExceptionMessage.INVALID_PASSWORD);
        }
    }

    private void checkEmailDuplication(String email) {
        if(userRepository.findByEmailAndIsDeletedFalse(email).isPresent()) {
            throw new UserException(UserExceptionMessage.ALREADY_EXIST_EMAIL);
        }
    }
}
