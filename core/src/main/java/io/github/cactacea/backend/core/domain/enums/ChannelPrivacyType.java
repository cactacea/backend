package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ChannelPrivacyType {
    everyone((byte)0),
    follows((byte)1),
    followers((byte)2),
    friends((byte)3);

    private byte value;

    ChannelPrivacyType(byte value) {
        this.value = value;
    }

    public static final List<ChannelPrivacyType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<ChannelPrivacyType>() {{
            add(everyone);
            add(follows);
            add(followers);
            add(friends);
        }} );
    }

    static public ChannelPrivacyType forName(byte value) {
        for (ChannelPrivacyType e : values()) {
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

