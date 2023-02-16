package org.rajman.authentication.enumeration;

import lombok.Getter;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

@Getter
public enum ActivityAuthEnum {
    SIGN_UP((short) 56), SIGN_IN((short) 104), SIGN_OUT_APP((short) 115), SIGN_OUT_SYSTEM((short) 105);

    private final short value;

    ActivityAuthEnum(short value) {
        this.value = value;
    }

    public static ActivityAuthEnum fromIndex(Short value) {
        return Arrays.stream(ActivityAuthEnum.values())
                .filter(z -> z.getValue() == value)
                .findAny()
                .orElse(null);
    }

    public static class Converter implements AttributeConverter<ActivityAuthEnum, Short> {

        @Override
        public Short convertToDatabaseColumn(ActivityAuthEnum attribute) {

            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public ActivityAuthEnum convertToEntityAttribute(Short dbData) {
            return ActivityAuthEnum.fromIndex(dbData);
        }
    }
}
