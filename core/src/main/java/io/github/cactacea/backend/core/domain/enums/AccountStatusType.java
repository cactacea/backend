package io.github.cactacea.backend.core.domain.enums;

import io.github.cactacea.backend.core.domain.models.AccountStatus;
import scala.collection.immutable.Seq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum AccountStatusType {
    normally((byte)0),
    deleted((byte)1),
    terminated((byte)2);

    private byte value;

    private AccountStatusType(byte value) {
        this.value = value;
    }

    static public AccountStatusType forName(byte value) {
        for (AccountStatusType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public byte toValue() {
        return value;
    }

    public static final List<AccountStatusType> all = Collections.unmodifiableList( new ArrayList<AccountStatusType>() {{
        add(normally);
        add(deleted);
        add(terminated);
    }} );

}
