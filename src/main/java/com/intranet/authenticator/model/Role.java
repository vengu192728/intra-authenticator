package com.intranet.authenticator.model;

public enum Role {
    ADMIN,
    USER;

    public static Role fromConfigValue(String value) {
        if (value == null || value.isBlank()) {
            return USER;
        }
        return switch (value.trim().toLowerCase()) {
            case "admin" -> ADMIN;
            case "user" -> USER;
            default -> throw new IllegalArgumentException("Invalid role: " + value);
        };
    }
}
