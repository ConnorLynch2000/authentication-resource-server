package org.rajman.authentication.configuration;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.rajman.authentication.model.dto.config.EncryptionKeyConfig;
import org.rajman.authentication.model.dto.config.RabbitConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppConfig {
    EncryptionKeyConfig encryptionKey = new EncryptionKeyConfig();
    RabbitConfig rabbit = new RabbitConfig();
}


