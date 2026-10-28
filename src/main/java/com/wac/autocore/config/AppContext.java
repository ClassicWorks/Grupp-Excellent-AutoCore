package com.wac.autocore.config;

import com.wac.autocore.security.Authenticator;
import com.wac.autocore.security.CurrentUserProvider;
import com.wac.autocore.service.GarageSystem;

public interface AppContext {
    GarageSystem garageSystem();
    Authenticator authenticator();
    CurrentUserProvider currentUserProvider();

    default void close() {

    }
}
