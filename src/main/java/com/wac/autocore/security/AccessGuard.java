package com.wac.autocore.security;

import com.wac.autocore.exceptions.NotAuthenticatedException;
import com.wac.autocore.exceptions.PermissionDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessGuard {
    private static final Logger log = LoggerFactory.getLogger(AccessGuard.class);

    private final CurrentUserProvider currentUserProvider;

    public AccessGuard(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    /** Throws if no user is logged in or the user lacks the permission */
    public AppUser require(Permission permission, String operation) {
        AppUser user = currentUserProvider.currentUser().orElseThrow(() -> {
            log.warn("Unauthenticated attempt: operation={}", operation);
            return new NotAuthenticatedException();
        });

        if (!user.role().has(permission)) {
            log.warn("Access denied: user={} role={} operation={} required={}",
                    user.username(), user.role(), operation, permission);
            throw new PermissionDeniedException();
        }

        log.debug("Access granted: user={} operation={}", user.username(), operation);
        return user;
    }

    /** For the UI, to hide buttons. Never throws and never logs. */
    public boolean has(Permission permission) {
        return currentUserProvider.currentUser()
                .map(user -> user.role().has(permission))
                .orElse(false);
    }
}
