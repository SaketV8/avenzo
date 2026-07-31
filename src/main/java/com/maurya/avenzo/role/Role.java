package com.maurya.avenzo.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(
            Set.of(
                    Permission.EVENT_READ,
                    Permission.EVENT_CREATE,
                    Permission.EVENT_UPDATE,
                    Permission.EVENT_DELETE
            )
    ),

    ADMIN(
            Set.of(
                    Permission.EVENT_READ,
                    Permission.EVENT_CREATE,
                    Permission.EVENT_UPDATE,
                    Permission.EVENT_DELETE,
                    Permission.EVENT_CHECKIN,

                    Permission.USER_READ,
                    Permission.USER_UPDATE,
                    Permission.USER_DELETE,

                    Permission.CHECK_HEALTH

            )
    );

    private final Set<Permission> permissions;

    // handled by lombok
    /*
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
    */

}
