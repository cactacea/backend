package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PermissionType {
    @JsonProperty("read_only") readOnly("read_only"),
    @JsonProperty("read_write") readWrite("read_write");

    private String value;

    private PermissionType(String value) {
        this.value = value;
    }

    static public PermissionType forName(String value) {
        for (PermissionType e : values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public String toValue() {
        return value;
    }
}
