package org.rajman.authentication.enumeration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AuthActionType {
    LoginOrSignUp(1),
    Login(2),
    SignUp(3);

    int index;

    public static AuthActionType fromIndex(Integer index) {
        return Arrays.stream(AuthActionType.values())
                .filter(authActionType -> authActionType.index == index)
                .findFirst().orElseGet(() -> AuthActionType.LoginOrSignUp);
    }
}
