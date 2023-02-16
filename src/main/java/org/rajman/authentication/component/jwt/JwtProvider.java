package org.rajman.authentication.component.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtProvider {

    public static final String USERNAME = "usn";
    JwtParser jwtParser;

    @Qualifier("HMAC")
    SecretKey key;

    ObjectMapper objectMapper;


    public String generateToken(long userId, String username, LocalDateTime expireIn) {
        return generateToken(userId, username, expireIn, false);
    }

    public Claims getAllClaimsFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public boolean jwtAuthenticate(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new MalformedJwtException("invalid token (token is refresh token)");
        }
        if (isTokenExpired(claims)) {
            throw new ExpiredJwtException(null, claims, "Token is expired");
        }

        return true;
    }

    private String generateToken(long userId, String username,
                                 LocalDateTime expireIn, boolean isRefreshToken) {
        Map<String, Object> claim = new HashMap<>();
        claim.put(USERNAME, username);

        JwtBuilder jwtBuilder = Jwts.builder().serializeToJsonWith(new JacksonSerializer<>(objectMapper));
        jwtBuilder = jwtBuilder.signWith(key);

        return jwtBuilder
                .setExpiration(getExpirationDate(expireIn))
                .setId(String.valueOf(userId))
                .setIssuedAt(getNow())
                .addClaims(claim)
                .setHeaderParam("type", "JWT")
                .compact();
    }

    private Date getExpirationDate(LocalDateTime expireIn) {
        Instant instant = expireIn.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private Date getNow() {
        Instant instant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public Boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
