package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReportType {
    @JsonProperty("0")none((byte)0),
    @JsonProperty("1")spam((byte)1),
    @JsonProperty("2")inappropriate((byte)2);

    private byte value;

    private ReportType(byte value) {
        this.value = value;
    }

    static public ReportType forName(byte value) {
        for (ReportType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public byte toValue() {
        return value;
    }
}
