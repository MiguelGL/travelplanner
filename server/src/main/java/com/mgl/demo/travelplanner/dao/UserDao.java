package com.mgl.demo.travelplanner.dao;

import com.mgl.demo.travelplanner.dao.support.BaseEntityDao;
import com.mgl.demo.travelplanner.entity.QUser;
import com.mgl.demo.travelplanner.entity.User;

public class UserDao extends BaseEntityDao<User, QUser, UserDao> {

    public UserDao() {
        super(User.class, QUser.user);
    }

    public boolean existsUserByEmail(String email) {
        return existsAccordingTo(pathBase().email.eq(email));
    }

    public User findByEmail(String email) {
        return findExisting(pathBase().email.eq(email));
    }

}
