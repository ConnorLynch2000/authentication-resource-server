package org.rajman.authentication.component.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.configuration.AppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeyProvider {

    AppConfig appConfig;

    @Bean("HMAC")
    public SecretKey getHmacKey() {
        return Keys.hmacShaKeyFor(appConfig.getEncryptionKey().getHMACKeySecret().getBytes(StandardCharsets.UTF_8));
    }
}