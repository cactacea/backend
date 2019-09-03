package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum MessageType {
    text((byte)0),
    medium((byte)1),
    stamp((byte)2),
    invited((byte)3),
    joined((byte)4),
    left((byte)5);

    private byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public static final List<MessageType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<MessageType>() {{
            add(text);
            add(medium);
            add(stamp);
            add(invited);
            add(joined);
            add(left);
        }} );
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