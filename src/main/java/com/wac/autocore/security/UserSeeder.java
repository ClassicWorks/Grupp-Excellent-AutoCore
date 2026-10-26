package com.wac.autocore.security;

import com.wac.autocore.data.UserRepo;
import com.wac.autocore.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class is purely for generating users. Check README on how to set password and information. */

public class UserSeeder {

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    private final UserRepo userRepo;
    private final PasswordHasher passwordHasher;

    public UserSeeder(UserRepo userRepo, PasswordHasher passwordHasher) {
        this.userRepo = userRepo;
        this.passwordHasher = passwordHasher;
    }

    public void seedTestUsers() {
        createIfMissing("admin", "AUTOCORE_ADMIN_PASSWORD", Role.ADMIN);
        createIfMissing("workshop", "AUTOCORE_WORKSHOP_PASSWORD", Role.WORKSHOP);
        createIfMissing("reader", "AUTOCORE_READER_PASSWORD", Role.READ_ONLY);
    }

    private void createIfMissing(String username, String envVariable, Role role) {
        if (userRepo.findByUsername(username).isPresent()){
            return;
        }

        String password = System.getenv(envVariable);
        if (password == null || password.isBlank()) {
            log.warn("Test user not created, environment variable missing: user={} variable={}",
                    username, envVariable);
            return;
        }

        userRepo.save(new User(username, passwordHasher.hash(password), role));
        log.info("Test user created: user={} role={}", username, role);
    }
}
