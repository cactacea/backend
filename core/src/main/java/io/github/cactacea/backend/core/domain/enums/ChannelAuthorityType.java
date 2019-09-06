package io.github.cactacea.backend.core.domain.enums;

public enum ChannelAuthorityType {
    organizer((byte)0),
    member((byte)1);

    public byte value;

    ChannelAuthorityType(byte value) {
        this.value = value;
    }

    static public ChannelAuthorityType forName(byte value) {
        for (ChannelAuthorityType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
