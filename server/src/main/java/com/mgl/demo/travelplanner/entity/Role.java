package com.mgl.demo.travelplanner.entity;

public enum Role {

    REGULAR_USER,
    MANAGER,
    ADMINISTRATOR;

    public static final String REGULAR_USER_NAME = "REGULAR_USER";
    public static final String MANAGER_NAME = "MANAGER";
    public static final String ADMINISTRATOR_NAME = "ADMINISTRATOR";

    public static final int ROLE_MAX_LEN = 32;

    boolean hasAllUserTripsAccess() {
        return this == ADMINISTRATOR;
    }

}
