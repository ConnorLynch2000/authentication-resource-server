package org.rajman.authentication.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.component.jwt.JwtProvider;
import org.rajman.authentication.model.dto.AuthenticationToken;
import org.rajman.authentication.model.dto.LoginDTO;
import org.rajman.authentication.model.entity.UserEntity;
import org.rajman.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Base64Utils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    JwtProvider jwtProvider;
    UserRepository userRepository;
    Base64.Encoder encoder = Base64.getEncoder();
    TransactionTemplate transactionTemplate;

    public AuthenticationToken login(LoginDTO loginDTO) {
        Optional<UserEntity> user = userRepository.findByUsername(loginDTO.username());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(encoder.encodeToString(loginDTO.password().getBytes()))) {
                return generateToken(user.get());
            }
            throw new IllegalArgumentException("Credential is not valid.");
        } else {
            UserEntity userEntity = transactionTemplate.execute(status ->
                    userRepository.save(UserEntity.builder()
                    .username(loginDTO.username())
                    .password(encoder.encodeToString(loginDTO.password().getBytes()))
                    .createdAt(LocalDateTime.now())
                    .build()));
            if (userEntity == null){
                throw new IllegalArgumentException("Credential is not valid.");
            }
            return generateToken(userEntity);
        }
    }

    public boolean authenticate(String token){
        return jwtProvider.jwtAuthenticate(token);
    }

    private AuthenticationToken generateToken(UserEntity user) {
        return AuthenticationToken.builder()
                .token(jwtProvider.generateToken(user.getId(), user.getUsername(), getAccessTokenExpirationDate()))
                .build();
    }

    private LocalDateTime getAccessTokenExpirationDate() {
        return LocalDateTime.now().plusHours(5);
    }
}
