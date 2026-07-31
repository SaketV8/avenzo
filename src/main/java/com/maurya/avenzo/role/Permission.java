package com.maurya.avenzo.role;

public enum Permission {
    // event permissions
    EVENT_READ,
    EVENT_CREATE,
    EVENT_UPDATE,
    EVENT_DELETE,
    EVENT_CHECKIN,

    // user permissions
    USER_READ,
    USER_UPDATE,
    USER_DELETE,

    // server health permissions
    CHECK_HEALTH
}
