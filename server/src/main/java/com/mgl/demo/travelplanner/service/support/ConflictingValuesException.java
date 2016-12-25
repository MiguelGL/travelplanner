package com.mgl.demo.travelplanner.service.support;

import javax.ws.rs.core.Response.Status;

public class ConflictingValuesException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public ConflictingValuesException() {
        super(Status.PRECONDITION_FAILED);
    }

}
