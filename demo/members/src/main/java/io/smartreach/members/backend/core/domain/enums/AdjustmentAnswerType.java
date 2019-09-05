package io.smartreach.members.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum AdjustmentAnswerType {
    yes((byte)0),
    maybeYes((byte)1),
    no((byte)2);

    private byte value;

    AdjustmentAnswerType(byte value) {
        this.value = value;
    }

    public static final List<AdjustmentAnswerType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<AdjustmentAnswerType>() {{
            add(yes);
            add(maybeYes);
            add(no);
        }} );
    }

    static public AdjustmentAnswerType forName(byte value) {
        for (AdjustmentAnswerType e : values()) {
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
