package com.wac.autocore.controller;

import com.wac.autocore.security.Authenticator;
import com.wac.autocore.util.LanguageManager;

public class LoginController {

    private final Authenticator authenticator;

    public LoginController(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Result<Void> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return Result.failure(LanguageManager.getString("login.error.empty"));
        }

        if (!authenticator.login(username.trim(), password)) {
            return Result.failure(LanguageManager.getString("login.error.invalid"));
        }

        return Result.success(null);
    }

    public void logout() {
        authenticator.logout();
    }
}