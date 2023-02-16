package org.rajman.authentication.component.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SigningKeyResolver implements io.jsonwebtoken.SigningKeyResolver {

    @Qualifier("HMAC")
    SecretKey key;

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        SignatureAlgorithm algorithm = SignatureAlgorithm.forName(jwsHeader.getAlgorithm());
        return resolveKey(algorithm);
    }

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
        SignatureAlgorithm algorithm = SignatureAlgorithm.forName(jwsHeader.getAlgorithm());
        return resolveKey(algorithm);
    }

    private Key resolveKey(SignatureAlgorithm algorithm) {
        return key;
    }
}
