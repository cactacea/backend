package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccountStatusType {
    @JsonProperty("0") singedUp(0),
    @JsonProperty("1") deleted(1),
    @JsonProperty("2") terminated(2);

    private long value;

    private AccountStatusType(long value) {
        this.value = value;
    }

    static public AccountStatusType forName(long value) {
        for (AccountStatusType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public long toValue() {
        return value;
    }
}
