package com.intranet.authenticator.model;

public record AuthResponse(
        boolean success,
        String username,
        String role,
        String message
) {
    public static AuthResponse success(String username, Role role) {
        return new AuthResponse(true, username, role.name().toLowerCase(), "Authentication successful");
    }

    public static AuthResponse failure(String message) {
        return new AuthResponse(false, null, null, message);
    }
}
