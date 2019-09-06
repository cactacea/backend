package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum UserStatusType {
    normally((byte)0),
    deleted((byte)1),
    terminated((byte)2);

    public byte value;

    UserStatusType(byte value) {
        this.value = value;
    }

    static public UserStatusType forName(byte value) {
        for (UserStatusType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
