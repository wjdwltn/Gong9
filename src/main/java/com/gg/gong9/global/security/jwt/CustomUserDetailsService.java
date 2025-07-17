package com.gg.gong9.global.security.jwt;

import com.gg.gong9.global.exception.exceptions.auth.AuthException;
import com.gg.gong9.global.exception.exceptions.auth.AuthExceptionMessage;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername");
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AuthException(AuthExceptionMessage.EMAIL_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
