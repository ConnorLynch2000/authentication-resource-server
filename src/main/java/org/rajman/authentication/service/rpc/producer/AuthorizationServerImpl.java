package org.rajman.authentication.service.rpc.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.rajman.authentication.component.jwt.JwtProvider;
import org.rajman.authentication.model.dto.UserDetails;
import org.rajman.authentication.service.legacy.OauthAuthenticateService;
import org.rajman.common.commonmodel.dto.UserCacheDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthorizationServerImpl implements AuthorizationServer {
    JwtProvider jwtProvider;

    OauthAuthenticateService oauthAuthenticateService;

    ObjectMapper objectMapper;

    @Override
    public Long getPlayerIdFromAccessToken(String accessToken) {
        try {
            Optional<UserCacheDTO> userCache = getUserCache(accessToken);
            return userCache.map(UserCacheDTO::getUserId).orElse(null);
        } catch (Exception e) {
            log.error("getPlayerIdFromAccessToken", e);
            return null;
        }
    }

    @Override
    public byte[] getUserFromAccessToken(String accessToken) throws JsonProcessingException {
        Optional<UserCacheDTO> userCache = getUserCache(accessToken);
        if (userCache.isPresent()) {
            return objectMapper.writeValueAsBytes(userCache.get());
        }
        return new byte[0];
    }

    private Optional<UserCacheDTO> getUserCache(String accessToken) {
        UserDetails userDetails;
        if (Strings.isBlank(accessToken)) {
            return Optional.empty();
        }
        try {
            if (jwtProvider.isJwtToken(accessToken)) {
                userDetails = jwtProvider.jwtAuthenticate(accessToken);
            } else {
                userDetails = oauthAuthenticateService.authenticateToken(accessToken);
            }
        } catch (Exception e) {
            log.error("error in authorization Server rpc with token {}", accessToken);
            log.trace("error in authorization Server rpc ", e);
            return Optional.empty();
        }

        return Optional.of(
                UserCacheDTO.builder()
                        .playerId(userDetails.getPlayerId())
                        .userId(userDetails.getUserId())
                        .ban(false)
                        .build()
        );
    }
}
