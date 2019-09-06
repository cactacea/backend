package io.github.cactacea.backend.core.domain.enums;

public enum MessageType {
    text((byte)0),
    medium((byte)1),
    stamp((byte)2),
    invited((byte)3),
    joined((byte)4),
    left((byte)5);

    public byte value;

    MessageType(byte value) {
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

}