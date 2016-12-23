package com.mgl.demo.travelplanner.service.support;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

@ApplicationException(rollback = true, inherited = true)
public abstract class ServiceException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    public ServiceException(Status status) {
        super(status);
    }

}
