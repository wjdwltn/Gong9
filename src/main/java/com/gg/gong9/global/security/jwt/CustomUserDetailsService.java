package com.gg.gong9.global.security.jwt;

import com.gg.gong9.global.exception.ExceptionMessage;
import com.gg.gong9.global.exception.exceptions.UserException;
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
                .orElseThrow(() -> new UserException(ExceptionMessage.INVALID_EMAIL));

        return new CustomUserDetails(user);
    }
}
