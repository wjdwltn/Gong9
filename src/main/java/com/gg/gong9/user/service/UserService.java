package com.gg.gong9.user.service;

import com.gg.gong9.auth.controller.dto.*;
import com.gg.gong9.auth.repository.VerificationCodeRepository;
import com.gg.gong9.global.exception.ExceptionMessage;
import com.gg.gong9.global.exception.exceptions.UserException;
import com.gg.gong9.global.security.jwt.JwtTokenProvider;
import com.gg.gong9.mail.service.MailService;
import com.gg.gong9.user.controller.dto.*;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import com.gg.gong9.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final MailService mailService;

    private static final long CODE_TTL = 300; //5분
    private static final String VERIFICATION_CODE_PREFIX = "verificationCode:";
    private static final String PASSWORD_RESET_CODE_PREFIX = "passwordResetCode:";

    //회원가입
    public UserResponse join(JoinRequest joinRequest){

        checkEmailDuplication(joinRequest.email());

        User user = User.builder()
                .username(joinRequest.username())
                .email(joinRequest.email())
                .password(bCryptPasswordEncoder.encode(joinRequest.password()))
                .address(joinRequest.toAddress())
                .userRole(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId());
    }

    //로그인
    public LoginResponse login(LoginRequest loginRequest){

        User user = findByEmailOrThrow(loginRequest.email());

        validatePasswordOrThrow(loginRequest.password(), user.getPassword());

        //JWT 토근 생성
        String accessToken = jwtTokenProvider.createToken(user);

        return new LoginResponse(user.getId(), accessToken);
    }

    //회원 정보 조회
    public User getUser(Long id){
        return findByIdOrThrow(id);
    }

    //회원 정보 수정
    @Transactional
    public void updateUser(long userId, UpdateUserRequest updateUserReq){
        User user = findByIdOrThrow(userId);

        user.updateUser(updateUserReq.username(), updateUserReq.toAddress());
    }

    //비밀번호 변경
    @Transactional
    public void updatePassword(long userId, ChangePasswordRequest changePasswordReq){
        User user = findByIdOrThrow(userId);

        validatePasswordOrThrow(changePasswordReq.currentPassword(), user.getPassword());

        user.updatePassword(bCryptPasswordEncoder.encode(changePasswordReq.newPassword()));

    }

    //회원 탈퇴
    @Transactional
    public void deleteUser(long userId){
        User user = findByIdOrThrow(userId);

        user.softDelete();
    }

    //이메일 인증 번호 요청
    public void sendEmailCode(EmailRequest request){
        String verificationCode = createVerificationCode();

        saveVerificationCode(VERIFICATION_CODE_PREFIX, request.email(), verificationCode, CODE_TTL);

        String title = "이메일 인증 코드";
        String content = "아래 인증 코드를 입력하여 이메일 인증을 완료하세요.\n\n인증 코드: {code}";

        sendEmailWithCode(request.email(), title, content, verificationCode);
    }

    //이메일 인증 코드 확인
    public void verifyEmailCode(EmailCheckRequest request){
        verifyCode(VERIFICATION_CODE_PREFIX, request.email(), request.code());
    }

    //비밀번호 재설정 요청
    public void sendPasswordResetCode(PasswordResetRequest request){
        if(!userRepository.existsByEmail(request.email())){
            throw new IllegalStateException("해당 이메일로 등록된 계정이 없습니다.");
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
                .orElseThrow(()->new IllegalStateException("유효하지 않은 인증 코드입니다."));

        if (!storedCode.equals(code)) {
            throw new IllegalStateException("인증 코드가 일치하지 않습니다.");
        }

        verificationCodeRepository.deleteCode(key);
    }



    //검증

    private User findByIdOrThrow(long id){
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new UserException(ExceptionMessage.USER_NOT_FOUND));
    }

    private User findByEmailOrThrow(String email){
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(()-> new UserException(ExceptionMessage.INVALID_EMAIL));
    }

    private void validatePasswordOrThrow(String password, String encodedPassword){
        if (!bCryptPasswordEncoder.matches(password, encodedPassword)) {
            throw new UserException(ExceptionMessage.INVALID_PASSWORD);
        }
    }

    private void checkEmailDuplication(String email) {
        if(userRepository.findByEmailAndIsDeletedFalse(email).isPresent()) {
            throw new UserException(ExceptionMessage.ALREADY_EXIST_EMAIL);
        }
    }

}
