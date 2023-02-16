package org.rajman.authentication.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.model.dto.config.RabbitConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitConfiguration {

    public static final String SCHEDULER_API_EXCHANGE = "scheduler-api";
    public static final String AUTH_QUEUE = "auth-queue";

    @Primary
    @Bean
    public ConnectionFactory connectionFactory(AppConfig appConfig){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        try{
            RabbitConfig config = appConfig.getRabbit();
            connectionFactory.setAddresses(config.getAddresses());
            connectionFactory.setUsername(config.getUsername());
            connectionFactory.setPassword(config.getPassword());
            connectionFactory.setVirtualHost(config.getVirtualHost());
        }catch (Exception e){
            log.error("Error in loading rabbit configuration." + e.getMessage());
        }
        return connectionFactory;
    }

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory){
        return new RabbitTemplate(factory);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(SCHEDULER_API_EXCHANGE, true, false);
    }

    @Bean
    public Queue authQueue(){
        return new Queue(AUTH_QUEUE, true);
    }

    @Bean
    public Binding authBinding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("auth");
    }
}
