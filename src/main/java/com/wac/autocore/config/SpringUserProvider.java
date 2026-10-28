package com.wac.autocore.config;

import com.wac.autocore.security.AppUser;
import com.wac.autocore.security.CurrentUserProvider;
import com.wac.autocore.security.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringUserProvider implements CurrentUserProvider {

    @Override
    public Optional<AppUser> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst()
                .map(authority -> new AppUser(authentication.getName(),
                        Role.valueOf(authority.substring("ROLE_".length()))));
    }
}
