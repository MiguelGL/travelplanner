package com.mgl.demo.travelplanner;

import java.security.Principal;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.mgl.demo.travelplanner.dao.UserDao;
import com.mgl.demo.travelplanner.entity.User;

public class LoggedInUserProducer {

    @Inject private Principal principal;

    @Inject private UserDao userDao;

    @Produces @LoggedIn @SessionScoped
    public User produceLoggedInUser() {
        return userDao.findByEmail(principal.getName());
    }

}
