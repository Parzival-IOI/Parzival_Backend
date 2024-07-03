package com.java.parzival.Enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    TEACHER("GUEST");

    private final String value;
    UserRole(String value) {
        this.value = value;
    }
}
