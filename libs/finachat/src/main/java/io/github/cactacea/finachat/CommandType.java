package io.github.cactacea.finachat;

public enum CommandType {
    connect((byte)0),
    message((byte)1),
    room((byte)2),
    disconnect((byte)3);

    private byte value;

    private CommandType(byte value) {
        this.value = value;
    }

    static public CommandType forName(byte value) {
        for (CommandType e : values()) {
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
