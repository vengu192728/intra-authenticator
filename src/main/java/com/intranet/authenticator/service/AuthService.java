package com.intranet.authenticator.service;

import com.intranet.authenticator.model.AuthResponse;
import com.intranet.authenticator.model.LoginRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final CredentialStore credentialStore;

    public AuthService(CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
    }

    public AuthResponse authenticate(LoginRequest request) {
        return credentialStore.find(request.username())
                .filter(credential -> constantTimeEquals(credential.password(), request.password()))
                .map(credential -> AuthResponse.success(credential.username(), credential.role()))
                .orElseGet(() -> AuthResponse.failure("Invalid username or password"));
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        return Objects.equals(expected, actual);
    }
}
