package io.smartreach.members.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum EventJoinType {
    none((byte)0),
    yes((byte)1),
    no((byte)2);

    private byte value;

    EventJoinType(byte value) {
        this.value = value;
    }

    public static final List<EventJoinType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<EventJoinType>() {{
            add(none);
            add(yes);
            add(no);
        }} );
    }

    static public EventJoinType forName(byte value) {
        for (EventJoinType e : values()) {
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
