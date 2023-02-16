package org.rajman.authentication.service.rpc.producer;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthorizationServer {
    Long getPlayerIdFromAccessToken(String accessToken);

    byte[] getUserFromAccessToken(String accessToken) throws JsonProcessingException;
}
