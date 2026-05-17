package com.intranet.authenticator.service;

import com.intranet.authenticator.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CredentialStoreTest {

    @Test
    void parsesUsernamePasswordAndRole() {
        CredentialStore.StoredCredential credential =
                CredentialStore.parseLine("admin:secret:admin", 1);

        assertEquals("admin", credential.username());
        assertEquals("secret", credential.password());
        assertEquals(Role.ADMIN, credential.role());
    }

    @Test
    void defaultsRoleToUserWhenOmitted() {
        CredentialStore.StoredCredential credential =
                CredentialStore.parseLine("bob:secret", 1);

        assertEquals(Role.USER, credential.role());
    }

    @Test
    void rejectsInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> CredentialStore.parseLine("invalid-line", 1));
    }
}
