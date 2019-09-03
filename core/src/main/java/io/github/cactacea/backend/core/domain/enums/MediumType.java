package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum MediumType {
    image((byte)0),
    movie((byte)1);

    private byte value;

    MediumType(byte value) {
        this.value = value;
    }

    public static final List<MediumType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<MediumType>() {{
            add(image);
            add(movie);
        }});
    }

    static public MediumType forName(byte value) {
        for (MediumType e : values()) {
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

