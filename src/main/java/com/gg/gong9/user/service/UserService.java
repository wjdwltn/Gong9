package com.gg.gong9.user.service;

import com.gg.gong9.global.exception.ExceptionMessage;
import com.gg.gong9.global.exception.exceptions.UserException;
import com.gg.gong9.user.controller.dto.*;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

    //검증

    private User findByIdOrThrow(long id){
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new UserException(ExceptionMessage.USER_NOT_FOUND));
    }

    public void validatePasswordOrThrow(String password, String encodedPassword){
        if (!bCryptPasswordEncoder.matches(password, encodedPassword)) {
            throw new UserException(ExceptionMessage.INVALID_PASSWORD);
        }
    }
}
