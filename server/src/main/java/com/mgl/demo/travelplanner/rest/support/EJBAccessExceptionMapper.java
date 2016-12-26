package com.mgl.demo.travelplanner.rest.support;

import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class EJBAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {

    @Override
    public Response toResponse(EJBAccessException exception) {
        log.warn("EJB access exception: {}", String.valueOf(exception));
        return Response.status(Status.FORBIDDEN).build();
    }

}
