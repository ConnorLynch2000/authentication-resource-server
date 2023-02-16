package org.rajman.authentication.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import org.rajman.authentication.component.jwt.SigningKeyResolver;
import org.rajman.authentication.controller.interceptor.ResponseTimeCheckToken;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BeanConfiguration {

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        eventMulticaster.setTaskExecutor(executorService);
        return eventMulticaster;
    }

    @Bean("jwtParser")
    public JwtParser getJwtDecoder(ObjectMapper objectMapper, SigningKeyResolver signingKeyResolver) {
        return Jwts.parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer<>(objectMapper))
                .setSigningKeyResolver(signingKeyResolver).build();
    }

    @Bean("refreshTokenExecutor")
    public ExecutorService getRefreshTokenExecutor() {
        return Executors.newFixedThreadPool(5);
    }

    @Bean
    public FilterRegistrationBean<ResponseTimeCheckToken> loggingFilter() {
        FilterRegistrationBean<ResponseTimeCheckToken> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ResponseTimeCheckToken());
        registrationBean.addUrlPatterns(
                "/token/check_token", "/oauth/check_token", "/authorization-server/oauth/check_token"
        );
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
