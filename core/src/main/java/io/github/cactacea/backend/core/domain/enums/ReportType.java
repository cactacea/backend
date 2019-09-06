package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ReportType {
    none((byte)0),
    spam((byte)1),
    inappropriate((byte)2);

    public byte value;

    ReportType(byte value) {
        this.value = value;
    }

    static public ReportType forName(byte value) {
        for (ReportType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
