package org.rajman.authentication.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthenticationToken(@JsonProperty("token")String token) {
}
