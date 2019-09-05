package io.smartreach.members.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum CommunicationType {
    app((byte)0),
    email((byte)1),
    phone((byte)2);

    private byte value;

    CommunicationType(byte value) {
        this.value = value;
    }

    public static final List<CommunicationType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<CommunicationType>() {{
            add(app);
            add(email);
            add(phone);
        }} );
    }

    static public CommunicationType forName(byte value) {
        for (CommunicationType e : values()) {
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
