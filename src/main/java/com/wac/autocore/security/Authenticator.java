package com.wac.autocore.security;

public interface Authenticator {
    boolean login(String username, String password);
    void logout();
}
