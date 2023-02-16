package org.rajman.authentication.service.rpc.consumer;

import org.rajman.common.genericexceptionhandler.model.GenericErrorException;


public interface MessageHandlerInterface {
    void sendNewUserMessages(Long playerId) throws GenericErrorException;
}

