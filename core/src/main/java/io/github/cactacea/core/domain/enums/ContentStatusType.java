package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ContentStatusType {
    @JsonProperty("0") unchecked((byte)0),
    @JsonProperty("1") accepted((byte)1),
    @JsonProperty("2") rejected((byte)2);

    private byte value;

    private ContentStatusType(byte value) {
        this.value = value;
    }

    static public ContentStatusType forName(byte value) {
        for (ContentStatusType e : values()) {
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
