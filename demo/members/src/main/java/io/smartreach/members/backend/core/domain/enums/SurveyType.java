package io.smartreach.members.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum SurveyType {
    radio((byte)0),
    checkBox((byte)1);

    private byte value;

    SurveyType(byte value) {
        this.value = value;
    }

    public static final List<SurveyType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<SurveyType>() {{
            add(radio);
            add(checkBox);
        }} );
    }

    static public SurveyType forName(byte value) {
        for (SurveyType e : values()) {
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
