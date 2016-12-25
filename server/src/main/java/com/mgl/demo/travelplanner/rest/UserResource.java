package com.mgl.demo.travelplanner.rest;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.mgl.demo.travelplanner.dao.UserDao;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.rest.UsersResource.UserCreateRequest;
import com.mgl.demo.travelplanner.service.UserService;

@Path("/sec/users/{userId}")
@Stateless
@RolesAllowed({Role.ADMINISTRATOR_NAME, Role.MANAGER_NAME})
public class UserResource {

    @Inject private UserDao userDao;

    @Inject private UserService userService;

    @GET
    public User getUser(@PathParam("userId") Long userId) {
        return userDao.findById(userId);
    }

    @PUT
    public User updateUser(@PathParam("userId") Long userId, UserCreateRequest userTemplate) {
        User user = userDao.findById(userId);
        return userService.updateUser(user, userTemplate,
                Optional.ofNullable(userTemplate.getPlainPassword()));
    }

    @DELETE
    public User deleteUser(@PathParam("userId") Long userId) {
        User user = userDao.findById(userId);
        userDao.delete(user);
        return user;
    }

}
