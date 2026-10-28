package com.wac.autocore.config;

import com.wac.autocore.security.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringAuthenticator implements Authenticator {
    private static final Logger log = LoggerFactory.getLogger(SpringAuthenticator.class);

    private final AuthenticationManager authenticationManager;

    public SpringAuthenticator(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Login successful: user={}", username);
            return true;
        } catch (AuthenticationException e) {
            log.warn("Login failed: user={}", username);
            return false;
        }
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("Logout: user={}", authentication.getName());
        }
        SecurityContextHolder.clearContext();
    }
}
