package io.github.cactacea.backend.core.domain.enums;

public enum FriendsSortType {
    friendsAt((byte)0),
    accountName((byte)1);

    private byte value;

    private FriendsSortType(byte value) {
        this.value = value;
    }

    static public FriendsSortType forName(byte value) {
        for (FriendsSortType e : values()) {
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
