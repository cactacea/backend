package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MediumType {
    @JsonProperty("0")image(0),
    @JsonProperty("1")movie(1);

    private long value;

    private MediumType(long value) {
        this.value = value;
    }

    static public MediumType forName(long value) {
        for (MediumType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public long toValue() {
        return value;
    }
}

