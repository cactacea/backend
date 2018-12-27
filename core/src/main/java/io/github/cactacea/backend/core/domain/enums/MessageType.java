package io.github.cactacea.backend.core.domain.enums;

public enum MessageType {
    text((byte)0),
    medium((byte)1),
    stamp((byte)2),
    groupInvitation((byte)3),
    groupJoined((byte)4),
    groupLeft((byte)5);

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