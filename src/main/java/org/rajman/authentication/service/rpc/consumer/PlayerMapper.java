package org.rajman.authentication.service.rpc.consumer;

public interface PlayerMapper {
    void updatePlayerPictureFromExternalUrl(long playerId, String externalProfileURL);
}

