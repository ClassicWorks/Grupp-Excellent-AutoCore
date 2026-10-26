package com.wac.autocore.security;

import java.util.Optional;

public interface CurrentUserProvider {
    Optional<AppUser> currentUser();
}
