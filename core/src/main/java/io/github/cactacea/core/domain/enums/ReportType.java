package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReportType {
    @JsonProperty("0")none(0),
    @JsonProperty("1")spam(1),
    @JsonProperty("2")inappropriate(2);

    private long value;

    private ReportType(long value) {
        this.value = value;
    }

    static public ReportType forName(long value) {
        for (ReportType e : values()) {
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
