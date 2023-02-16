package org.rajman.authentication.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.component.jwt.JwtProvider;
import org.rajman.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    JwtProvider jwtProvider;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Qualifier("refreshTokenExecutor")
    ExecutorService executorService;

    private LocalDateTime getAccessTokenExpirationDate() {
        return LocalDateTime.now().plusHours(5);
    }

    private LocalDateTime getRefreshTokenExpirationDate() {
        return LocalDateTime.now().plusYears(5);
    }
}
