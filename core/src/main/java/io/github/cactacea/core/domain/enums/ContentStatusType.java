package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ContentStatusType {
    @JsonProperty("0") unchecked(0),
    @JsonProperty("1") accepted(1),
    @JsonProperty("2") rejected(2);

    private long value;

    private ContentStatusType(long value) {
        this.value = value;
    }

    static public ContentStatusType forName(long value) {
        for (ContentStatusType e : values()) {
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
