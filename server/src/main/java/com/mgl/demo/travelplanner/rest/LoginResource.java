package com.mgl.demo.travelplanner.rest;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.mgl.demo.travelplanner.dao.UserDao;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.cache.NoCache;

@Path("/login")
@NoCache
@Stateless
@Slf4j
public class LoginResource {

    @Inject private UserDao userDao;

    @Inject private UserService userService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public User register(
            @FormParam("email") String email,
            @FormParam("password") String password,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") @DefaultValue("") String lastName) {
        User user = userService.createRegularUser(email, password, firstName, lastName);
        return user;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public User login(
            @FormParam("email") @DefaultValue("") String email,
            @FormParam("password") @DefaultValue("") String password,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) {
        try {
            request.login(email, password);
            boolean authenticated = request.authenticate(response);
            if (!authenticated) {
                log.warn("authentication error | email={}", email);
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
        } catch (ServletException | IOException ex) {
            log.warn("login error | email={}", email, ex);
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        return userDao.findByEmail(email);
    }

    private void ensureLoggedIn(HttpServletRequest request) {
        if (!request.isUserInRole("**")) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getLoggedInUser(@Context HttpServletRequest request) {
        ensureLoggedIn(request);
        String email = request.getUserPrincipal().getName();
        return userDao.findByEmail(email);
    }

    @DELETE
    public void logout(@Context HttpServletRequest request) {
        ensureLoggedIn(request);
        try {
            request.logout();
        } catch (ServletException ex) {
            log.warn("logout error", ex);
            throw new RuntimeException("Error logging out", ex);
        } finally {
            request.getSession(false).invalidate();
        }
    }

}
