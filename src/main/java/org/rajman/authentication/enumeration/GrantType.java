package org.rajman.authentication.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.rajman.authentication.controller.exception.ErrorMessage;
import org.rajman.common.genericexceptionhandler.model.GenericErrorException;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum GrantType {

    PASSWORD("password"),
    REFRESH("refresh_token");

    public static GrantType fromSlug(String slug) throws GenericErrorException {
        return Arrays.stream(values()).filter(grantType -> grantType.getSlug().equalsIgnoreCase(slug))
                .findAny().orElseThrow(() -> new GenericErrorException(ErrorMessage.UNEXPECTED_VALUE_FOR_GRANT_TYPE));
    }

    String slug;


}
