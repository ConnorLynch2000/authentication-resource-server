package org.rajman.authentication.component.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.controller.exception.ErrorMessage;
import org.rajman.authentication.model.dto.UserDetails;
import org.rajman.authentication.util.StringEncryptUtil;
import org.rajman.common.genericexceptionhandler.model.GenericErrorException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtProvider {

    public static final String REFRESH_TOKEN = "rft";
    public static final String USERNAME = "usn";
    JwtParser jwtParser;

    @Qualifier("HMAC")
    SecretKey key;

    ObjectMapper objectMapper;

    BlackListService blackListService;

    AuthorizationService authorizationService;


    public String generateToken(long userId, String username, LocalDateTime expireIn) {
        return generateToken(userId, username, expireIn, false);
    }

    public String generateRefreshToken(long userId, String username, LocalDateTime expireIn) {
        return generateToken(userId,username, expireIn, true);
    }

    public boolean isJwtToken(String token) {
        return token.split("\\.").length > 1;
    }

    public Claims getAllClaimsFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public UserDetails jwtAuthenticate(String token) throws GenericErrorException {
        Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            throw new GenericErrorException(ErrorMessage.InvalidToken);
        }
        if (isTokenExpired(claims)) {
            throw new ExpiredJwtException(null, claims, "Token is expired");
        }

        if (isRefreshTokenFromClaims(claims)) {
            throw new MalformedJwtException("invalid token (token is refresh token)");
        }

        return UserDetails.builder()
                .userId(Long.parseLong(claims.getId()))
                .username(StringEncryptUtil.decrypt(claims.get(JwtProvider.USERNAME, String.class)))
                .build();
    }

    private String generateToken(long userId, String username,
                                 LocalDateTime expireIn, boolean isRefreshToken) {
        Map<String, Object> claim = new HashMap<>();
        claim.put(USERNAME, StringEncryptUtil.encrypt(username));

        if (isRefreshToken) {
            claim.put(REFRESH_TOKEN, UUID.randomUUID().toString());
        }

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

    public long getUserIdFromClaims(Claims claims) {
        return Long.parseLong(claims.getId());
    }

    public boolean isRefreshTokenFromClaims(Claims claims) {
        return claims.containsKey(JwtProvider.REFRESH_TOKEN);
    }

    public Boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
