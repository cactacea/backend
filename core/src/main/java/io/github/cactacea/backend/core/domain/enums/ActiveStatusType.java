package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ActiveStatusType {
    active((byte)0),
    inactive((byte)1);

    public byte value;

    ActiveStatusType(byte value) {
        this.value = value;
    }

    static public ActiveStatusType forName(byte value) {
        for (ActiveStatusType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
