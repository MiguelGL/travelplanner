package com.mgl.demo.travelplanner.service;

import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import com.mgl.demo.travelplanner.service.support.InvalidValuesException;
import com.mgl.demo.travelplanner.service.support.EntityAlreadyExistsException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.mgl.demo.travelplanner.dao.UserDao;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;

@Stateless
@Slf4j
@RolesAllowed({Role.ADMINISTRATOR_NAME})
public class UserService {

    @Inject private UserDao userDao;

    public User createUser(
            @NotBlank String email,
            @NotBlank String plainPassword,
            Role role,
            @NotBlank String firstName,
            String lastName) {
        if (!User.isValidPlainPassword(plainPassword)) {
            log.warn("invalid password | email={}", email);
            throw new InvalidValuesException("password", plainPassword);
        }

        if (userDao.existsUserByEmail(email)) {
            log.warn("already exists | email={}", email);
            throw new EntityAlreadyExistsException("email", email);
        }

        String password = User.encryptPlainPassword(plainPassword);
        User user = new User(email, password, role, firstName, lastName);
        log.info("create | email={}", email);
        userDao.create(user);
        return user;
    }

    @PermitAll
    public User createRegularUser(
            @NotBlank String email,
            @NotBlank String plainPassword,
            @NotBlank String firstName,
            String lastName) {
        return createUser(email, plainPassword, Role.REGULAR_USER, firstName, lastName);
    }

    public User createAdminUser(
            @NotBlank String email,
            @NotBlank String plainPassword,
            @NotBlank String firstName,
            String lastName) {
        return createUser(email, plainPassword, Role.ADMINISTRATOR, firstName, lastName);
    }

    public User createManagerUser(
            @NotBlank String email,
            @NotBlank String plainPassword,
            @NotBlank String firstName,
            String lastName) {
        return createUser(email, plainPassword, Role.MANAGER, firstName, lastName);
    }

    public User updateUser(User user, User userTemplate, Optional<String> maybePlainPassword) {
        user.prepareForUpdate(userTemplate);
        maybePlainPassword.ifPresent(plainPassword -> 
            user.setPassword(User.validateAndEncryptPlainPassword(plainPassword))
        );
        return userDao.update(user);
    }

}
