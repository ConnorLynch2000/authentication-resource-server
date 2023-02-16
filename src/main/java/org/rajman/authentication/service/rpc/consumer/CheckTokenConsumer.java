package org.rajman.authentication.service.rpc.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.configuration.RabbitConfiguration;
import org.rajman.authentication.service.TokenService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CheckTokenConsumer {
    TokenService tokenService;
    ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfiguration.AUTH_QUEUE, concurrency = "1")
    public boolean authenticate(byte[] tokenByte){
        if(tokenByte == null){
            log.error("Null token in authentication.");
            return false;
        }
        String token;
        try {
            token = objectMapper.readValue(tokenByte, String.class);
        }catch (Exception e){
            log.error("Error in mapping token. {}", e.getMessage());
            return false;
        }
        return tokenService.authenticate(token);
    }
}
