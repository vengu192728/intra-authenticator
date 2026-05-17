package com.intranet.authenticator.controller;

import com.intranet.authenticator.model.AuthResponse;
import com.intranet.authenticator.model.LoginRequest;
import com.intranet.authenticator.service.AuthService;
import com.intranet.authenticator.service.CredentialStore;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final CredentialStore credentialStore;

    public AuthController(AuthService authService, CredentialStore credentialStore) {
        this.authService = authService;
        this.credentialStore = credentialStore;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        if (response.success()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "intra-authenticator"
        );
    }

    @PostMapping("/auth/reload")
    public Map<String, Object> reloadCredentials() {
        credentialStore.reload();
        return Map.of("reloaded", true);
    }
}
