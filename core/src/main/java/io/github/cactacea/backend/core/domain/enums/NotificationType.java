package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum NotificationType {
    operator((byte)0),
    invitation((byte)1),
    friendRequest((byte)2),
    feed((byte)3),
    feedReply((byte)4),
    commentReply((byte)5);

    private byte value;

    private NotificationType(byte value) {
        this.value = value;
    }

    static public NotificationType forName(byte value) {
        for (NotificationType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public byte toValue() {
        return value;
    }

    public static final List<NotificationType> all = Collections.unmodifiableList(new ArrayList<NotificationType>() {{
        add(operator);
        add(invitation);
        add(friendRequest);
        add(feed);
        add(feedReply);
        add(commentReply);
    }} );

}
