package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PermissionType {
    basic("basic"),                     // to read a user’s profile info and media
    comments("comments"),               // to post and delete comments on a user’s behalf
    messages("messages"),               // to post and delete messages on a user's behalf
    followerList("follower_list"),      // to read the list of followers and followed-by users
    likes("likes"),                     // to like and unlike media on a user’s behalf
    publicContent("public_content"),    // to read any public profile info and media on a user’s behalf
    relationships("relationships");     // to follow and unfollow accounts on a user’s behalf

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
