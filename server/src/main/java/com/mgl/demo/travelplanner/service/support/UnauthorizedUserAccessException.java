package com.mgl.demo.travelplanner.service.support;

import javax.ws.rs.core.Response.Status;

public class UnauthorizedUserAccessException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedUserAccessException() {
        super(Status.FORBIDDEN);
    }

}
