package com.wac.autocore.data;

import com.wac.autocore.model.User;

import java.util.Optional;

public interface UserRepo {
    User save(User user);
    Optional<User> findByUsername(String username);
}
