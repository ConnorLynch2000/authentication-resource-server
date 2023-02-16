package org.rajman.authentication.model.dto.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RabbitConfig {
    String addresses;
    String username;
    String password;
    String virtualHost;
}
