package org.rajman.authentication.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum DeviceEnum {
    ANDROID("androidVerifier"),
    IOS("iosVerifier"),
    WEB("webVerifier");

    String beanName;

    public static DeviceEnum of(String value) {
        return Arrays.stream(DeviceEnum.values())
                .filter(deviceEnum ->
                        deviceEnum.toString().equalsIgnoreCase(value)
                ).findFirst().orElse(ANDROID);
    }
}
