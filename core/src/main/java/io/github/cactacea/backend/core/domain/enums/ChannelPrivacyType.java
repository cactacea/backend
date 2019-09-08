package io.github.cactacea.backend.core.domain.enums;

public enum ChannelPrivacyType {
    everyone((byte)0),
    follows((byte)1),
    followers((byte)2),
    friends((byte)3);

    public byte value;

    ChannelPrivacyType(byte value) {
        this.value = value;
    }

    static public ChannelPrivacyType forName(byte value) {
        for (ChannelPrivacyType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}

