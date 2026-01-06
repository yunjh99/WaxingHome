package com.example.waxingweb.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ADMIN"),
    USER("USER");

    private final String key;

    public static Role of(String value) {
        for (Role role : values()) {
            if (role.key.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 Role 값입니다: " + value);
    }
}
