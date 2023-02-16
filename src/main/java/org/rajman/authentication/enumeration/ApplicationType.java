package org.rajman.authentication.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ApplicationType {
    NeshanApp(2), Other(4), NMapApp(5);

    int index;

    public static ApplicationType fromIndex(int index) {
        return Arrays.stream(ApplicationType.values()).filter(applicationType -> applicationType.index == index)
                .findFirst().orElse(Other);
    }

    public int getIndex() {
        return index;
    }

}
