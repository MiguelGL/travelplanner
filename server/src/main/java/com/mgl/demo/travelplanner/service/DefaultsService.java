package com.mgl.demo.travelplanner.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.mgl.demo.travelplanner.dao.UserDao;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Startup
@Slf4j
public class DefaultsService {

    @Inject private UserDao userDao;

    @Inject private UserService userService;

    @PostConstruct
    public void createDefaultUsers() {
        if (userDao.existsAdministrator()) {
            log.info("Default administrator user exists, doing nothing");
        } else {
            log.info("Creating default administrator user");
            userService.createAdminUser("admin@email.com", "adminpass", "Administrator", "");
        }

        if (userDao.existsManager()) {
            log.info("Default manager user exists, doing nothing");
        } else {
            log.info("Creating default manager user");
            userService.createManagerUser("manager@email.com", "managerpass", "Manager", "");
        }
    }

}
