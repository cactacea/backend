package io.github.cactacea.backend.core.domain.enums;

public enum ReportType {
    none((byte)0),
    spam((byte)1),
    inappropriate((byte)2);

    private byte value;

    private ReportType(byte value) {
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

    public byte toValue() {
        return value;
    }
}
