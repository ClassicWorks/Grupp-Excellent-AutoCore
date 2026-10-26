package com.wac.autocore.security;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMIN(EnumSet.allOf(Permission.class)),

    WORKSHOP(EnumSet.of(
            Permission.READ,
            Permission.MANAGE_CUSTOMERS,
            Permission.MANAGE_VEHICLES,
            Permission.MANAGE_WORK_ORDERS)),

    READ_ONLY(EnumSet.of(Permission.READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean has(Permission permission){
        return permissions.contains(permission);
    }
}
