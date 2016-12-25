package com.mgl.demo.travelplanner.dao;

import java.util.List;
import java.util.Optional;

import com.mgl.demo.travelplanner.dao.support.BaseEntityDao;
import com.mgl.demo.travelplanner.entity.QUser;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.rest.Pagination.OrderBySpec;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.RequiredArgsConstructor;

public class UserDao extends BaseEntityDao<Long, User, QUser> {

    @RequiredArgsConstructor
    public static enum OrderByField {
        
        EMAIL(QUser.user.email),
        FIRST_NAME(QUser.user.firstName),
        LAST_NAME(QUser.user.lastName);

        public static final String DEFAULT = "EMAIL";

        public static OrderByField defaultField() {
            return EMAIL;
        }

        private final ComparableExpressionBase<?> path;

        public Optional<OrderSpecifier<?>> maybeBuildOrderSpecifier(OrderBySpec spec) {
            if (spec.isOrdered()) {
                switch (spec) {
                    case ASC:
                        return Optional.of(path.asc());
                    case DESC:
                        return Optional.of(path.desc());
                    default:
                        throw new UnsupportedOperationException(String.valueOf(spec));
                }
            } else {
                return Optional.empty();
            }
        }
    }

    public UserDao() {
        super(User.class, QUser.user);
    }

    public boolean existsUserByEmail(String email) {
        return existsAccordingTo(pathBase().email.eq(email));
    }

    public boolean existsAdministrator() {
        return existsAccordingTo(pathBase().role.eq(Role.ADMINISTRATOR));
    }

    public boolean existsManager() {
        return existsAccordingTo(pathBase().role.eq(Role.MANAGER));
    }

    public User findByEmail(String email) {
        return findExisting(pathBase().email.eq(email));
    }

    public List<User> findAll(OrderByField orderByField, OrderBySpec orderBySpec, 
            long offset, long limit) {
        Optional<OrderSpecifier<?>> maybeOrder = orderByField.maybeBuildOrderSpecifier(orderBySpec);

        return find(Optional.of(offset), Optional.of(limit), maybeOrder);
    }

}
