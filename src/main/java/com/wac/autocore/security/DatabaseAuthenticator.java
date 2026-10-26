package com.wac.autocore.security;

import com.wac.autocore.data.UserRepo;
import com.wac.autocore.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DatabaseAuthenticator implements Authenticator {
    private static final Logger log = LoggerFactory.getLogger(DatabaseAuthenticator.class);

    private final UserRepo userRepo;
    private final AppSession session;
    private final PasswordHasher passwordHasher;

    public DatabaseAuthenticator(UserRepo userRepo, AppSession session, PasswordHasher passwordHasher) {
        this.userRepo = userRepo;
        this.session = session;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public boolean login(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);

        if (user.isPresent() && passwordHasher.matches(password, user.get().getPasswordHash())) {
            session.login(new AppUser(user.get().getUsername(), user.get().getRole()));
            log.info("Login successful: user={}", username);
            return true;
        }

        log.warn("Login failed: user={}", username);
        return false;
    }

    @Override
    public void logout() {
        session.currentUser().ifPresent(user -> log.info("Logout: user={}", user.username()));
        session.logout();
    }
}
