package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ChannelAuthorityType {
    organizer((byte)0),
    member((byte)1);

    private byte value;

    ChannelAuthorityType(byte value) {
        this.value = value;
    }

    public static final List<ChannelAuthorityType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<ChannelAuthorityType>() {{
            add(organizer);
            add(member);
        }} );
    }

    static public ChannelAuthorityType forName(byte value) {
        for (ChannelAuthorityType e : values()) {
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
