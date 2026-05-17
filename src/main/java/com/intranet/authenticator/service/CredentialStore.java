package com.intranet.authenticator.service;

import com.intranet.authenticator.config.AuthProperties;
import com.intranet.authenticator.model.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CredentialStore {

    private static final Logger log = LoggerFactory.getLogger(CredentialStore.class);

    private final String credentialsLocation;
    private final ResourceLoader resourceLoader;
    private Map<String, StoredCredential> credentials = Map.of();

    public CredentialStore(AuthProperties authProperties, ResourceLoader resourceLoader) {
        this.credentialsLocation = authProperties.getCredentialsFile();
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    void load() {
        reload();
    }

    public synchronized void reload() {
        try {
            List<String> lines = readLines();
            Map<String, StoredCredential> loaded = new HashMap<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                int lineNumber = i + 1;

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                StoredCredential credential = parseLine(line, lineNumber);
                String key = credential.username().toLowerCase();
                if (loaded.containsKey(key)) {
                    throw new IllegalStateException(
                            "Duplicate username in credentials file at line " + lineNumber);
                }
                loaded.put(key, credential);
            }

            credentials = Collections.unmodifiableMap(loaded);
            log.info("Loaded {} credential(s) from {}", credentials.size(), credentialsLocation);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load credentials from " + credentialsLocation, ex);
        }
    }

    private List<String> readLines() throws IOException {
        if (credentialsLocation.startsWith("classpath:")) {
            Resource resource = resourceLoader.getResource(credentialsLocation);
            if (!resource.exists()) {
                log.warn("Credentials resource not found at {}. No accounts loaded.", credentialsLocation);
                return List.of();
            }
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().toList();
            }
        }

        Path credentialsPath = Path.of(credentialsLocation).toAbsolutePath().normalize();
        if (!Files.isRegularFile(credentialsPath)) {
            log.warn("Credentials file not found at {}. No accounts loaded.", credentialsPath);
            return List.of();
        }
        return Files.readAllLines(credentialsPath, StandardCharsets.UTF_8);
    }

    public Optional<StoredCredential> find(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(credentials.get(username.toLowerCase()));
    }

    public String getCredentialsLocation() {
        return credentialsLocation;
    }

    static StoredCredential parseLine(String line, int lineNumber) {
        int firstColon = line.indexOf(':');
        if (firstColon <= 0 || firstColon == line.length() - 1) {
            throw new IllegalArgumentException(
                    "Invalid credentials format at line " + lineNumber + ". Expected username:password[:role]");
        }

        String username = line.substring(0, firstColon).trim();
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username is empty at line " + lineNumber);
        }

        String remainder = line.substring(firstColon + 1);
        int secondColon = remainder.indexOf(':');

        String password;
        Role role;

        if (secondColon < 0) {
            password = remainder;
            role = Role.USER;
        } else {
            password = remainder.substring(0, secondColon);
            String roleValue = remainder.substring(secondColon + 1).trim();
            role = Role.fromConfigValue(roleValue);
        }

        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password is empty at line " + lineNumber);
        }

        return new StoredCredential(username, password, role);
    }

    public record StoredCredential(String username, String password, Role role) {
    }
}
