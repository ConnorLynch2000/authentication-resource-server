package org.rajman.authentication.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import org.rajman.authentication.component.jwt.SigningKeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean("jwtParser")
    public JwtParser getJwtDecoder(ObjectMapper objectMapper, SigningKeyResolver signingKeyResolver) {
        return Jwts.parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer<>(objectMapper))
                .setSigningKeyResolver(signingKeyResolver).build();
    }
}
