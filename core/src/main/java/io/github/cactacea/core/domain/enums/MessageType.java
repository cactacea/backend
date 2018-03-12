package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    text((byte)0),
    medium((byte)1),
    stamp((byte)2),
    invitation((byte)3),
    joined((byte)4),
    left((byte)5);

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