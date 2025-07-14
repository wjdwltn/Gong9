package com.gg.gong9.user.controller;

import com.gg.gong9.user.controller.dto.*;
import com.gg.gong9.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request)
    {
        UserResponse userId = userService.login(loginRequest);
        addMemberInSession(request, userId);

        return ResponseEntity.ok(userId);

    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    //회원 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    private void addMemberInSession(HttpServletRequest request, UserResponse loginUser) {
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);
    }

    //회원 정보 수정
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId,
                                             @Valid @RequestBody UpdateUserRequest updateUserRequest)
    {
        userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok("회원 정보 수정 성공");
    }

    //비밀번호 변경
    @PutMapping("{userId}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @Valid @RequestBody ChangePasswordRequest changePasswordRequest)
    {
        userService.updatePassword(userId, changePasswordRequest);
        return ResponseEntity.ok("회원 비밀번호 변경 성공");
    }

    // 회원 탈퇴 (Soft Delete)
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }


}
