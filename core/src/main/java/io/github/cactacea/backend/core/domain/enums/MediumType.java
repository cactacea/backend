package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum MediumType {
    image((byte)0),
    movie((byte)1);

    public byte value;

    MediumType(byte value) {
        this.value = value;
    }

    static public MediumType forName(byte value) {
        for (MediumType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}

