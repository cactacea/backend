package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum GroupAuthorityType {
    organizer((byte)0),
    member((byte)1);

    private byte value;

    private GroupAuthorityType(byte value) {
        this.value = value;
    }

    static public GroupAuthorityType forName(byte value) {
        for (GroupAuthorityType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public byte toValue() {
        return value;
    }

    public static final List<GroupAuthorityType> all = Collections.unmodifiableList(new ArrayList<GroupAuthorityType>() {{
        add(organizer);
        add(member);
    }} );

}
