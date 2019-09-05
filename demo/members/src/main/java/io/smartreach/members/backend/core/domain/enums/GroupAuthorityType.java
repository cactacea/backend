package io.smartreach.members.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum GroupAuthorityType {
    event((byte)0),
    group((byte)1),
    schedule((byte)(1 << 1)),
    survey((byte)(1 << 2)),
    task((byte)(1 << 3));

    private byte value;

    GroupAuthorityType(byte value) {
        this.value = value;
    }

    public static final List<GroupAuthorityType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<GroupAuthorityType>() {{
            add(event);
            add(group);
            add(schedule);
            add(survey);
            add(task);
        }} );
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

}

// organize_event
// organize_group
// organize_schedule
// organize_survey
// organize_task

