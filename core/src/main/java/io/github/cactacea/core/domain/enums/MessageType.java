package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    @JsonProperty("0") text((byte)0),
    @JsonProperty("1") medium((byte)1),
    @JsonProperty("2") stamp((byte)2),
    @JsonProperty("3") invitation((byte)3),
    @JsonProperty("4") joined((byte)4),
    @JsonProperty("5") left((byte)5);

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    static public MessageType forName(byte value) {
        for (MessageType e : values()) {
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