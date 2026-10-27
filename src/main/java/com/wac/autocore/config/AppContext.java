package com.wac.autocore.config;

import com.wac.autocore.service.GarageSystem;

public interface AppContext {
    GarageSystem garageSystem();

    default void close() {

    }
}
