package com.wac.autocore.security;

import java.util.Optional;

public class AppSession implements CurrentUserProvider {

    private static final AppSession INSTANCE = new AppSession();

    private volatile AppUser currentUser;

    private AppSession() {

    }

    public static AppSession getInstance() {
        return INSTANCE;
    }

    public void login(AppUser user) {
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    @Override
    public Optional<AppUser> currentUser() {
        return Optional.ofNullable(currentUser);
    }
}
