package io.smartreach.members.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum TaskStatusType {
    none((byte)0),
    doing((byte)0),
    done((byte)1);

    private byte value;

    TaskStatusType(byte value) {
        this.value = value;
    }

    public static final List<TaskStatusType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<TaskStatusType>() {{
            add(none);
            add(doing);
            add(done);
        }} );
    }

    static public TaskStatusType forName(byte value) {
        for (TaskStatusType e : values()) {
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
