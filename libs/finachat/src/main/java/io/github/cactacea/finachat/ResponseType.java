package io.github.cactacea.finachat;

public enum ResponseType {
    operationSucceed((byte)0),
    operationFailed((byte)1),
    messageArrived((byte)2),
    memberJoined((byte)3),
    memberLeft((byte)4);

    private byte value;

    private ResponseType(byte value) {
        this.value = value;
    }

    static public ResponseType forName(byte value) {
        for (ResponseType e : values()) {
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
