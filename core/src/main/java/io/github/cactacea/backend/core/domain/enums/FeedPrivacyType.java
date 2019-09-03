package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum FeedPrivacyType {
    everyone((byte)0),
    followers((byte)1),
    friends((byte)2),
    self((byte)3);

    private byte value;

    FeedPrivacyType(byte value) {
        this.value = value;
    }

    public static final List<FeedPrivacyType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<FeedPrivacyType>() {{
            add(everyone);
            add(followers);
            add(friends);
            add(self);
        }} );
    }

    static public FeedPrivacyType forName(byte value) {
        for (FeedPrivacyType e : values()) {
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
