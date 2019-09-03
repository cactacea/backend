package io.github.cactacea.backend.core.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum PushNotificationType {
    message((byte)0),
    nonDisplay((byte)1),
    image((byte)2),
    invitation((byte)3),
    request((byte)4),
    feed((byte)5),
    feedReply((byte)6),
    commentReply((byte)7);

    private byte value;

    PushNotificationType(byte value) {
        this.value = value;
    }

    public static final List<PushNotificationType> all;

    static {
        all = Collections.unmodifiableList(new ArrayList<PushNotificationType>() {{
            add(message);
            add(nonDisplay);
            add(image);
            add(invitation);
            add(request);
            add(feed);
            add(feedReply);
            add(commentReply);
        }} );
    }

    static public PushNotificationType forName(byte value) {
        for (PushNotificationType e : values()) {
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
