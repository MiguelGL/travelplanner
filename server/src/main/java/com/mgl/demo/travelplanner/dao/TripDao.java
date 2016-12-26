package com.mgl.demo.travelplanner.dao;

import static com.mgl.demo.travelplanner.entity.QUser.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mgl.demo.travelplanner.dao.support.BaseEntityDao;
import com.mgl.demo.travelplanner.entity.QTrip;
import com.mgl.demo.travelplanner.entity.Trip;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.rest.support.Pagination.OrderBySpec;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.RequiredArgsConstructor;

public class TripDao extends BaseEntityDao<Long, Trip, QTrip> {

    @RequiredArgsConstructor
    public static enum OrderByField {

        USER(QTrip.trip.user.email),
        DESTINATION(QTrip.trip.destination.name),
        START_DATE(QTrip.trip.startDate),
        END_DATE(QTrip.trip.endDate);

        public static final String DEFAULT = "START_DATE";

        public static OrderByField defaultField() {
            return START_DATE;
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

    public TripDao() {
        super(Trip.class, QTrip.trip);
    }

    public List<Trip> findUserTrips(User user,
            OrderByField orderByField, OrderBySpec orderBySpec,
            long offset, long limit) {
        return find(
                Optional.of(offset),
                Optional.of(limit),
                orderByField.maybeBuildOrderSpecifier(orderBySpec),
                pathBase().user.eq(user));
    }

    public List<Trip> findAllTrips(User user,
            OrderByField orderByField, OrderBySpec orderBySpec,
            long offset, long limit) {
        return find(
                Optional.of(offset),
                Optional.of(limit),
                orderByField.maybeBuildOrderSpecifier(orderBySpec));
    }

    public boolean existsOverlappingUserTrip(User user,
            LocalDate startDate, LocalDate endDate) {
        return existsAccordingTo(
                pathBase().user.eq(user)
                .and(
                        pathBase().startDate.between(startDate, endDate)
                        .or(pathBase().endDate.between(startDate, endDate))
                        .or(pathBase().startDate.eq(startDate).or(pathBase().startDate.before(startDate))
                            .and(pathBase().endDate.eq(endDate).or(pathBase().endDate.after(endDate))))
                        .or(pathBase().startDate.eq(startDate).or(pathBase().startDate.after(startDate))
                            .and(pathBase().endDate.eq(endDate).or(pathBase().endDate.before(endDate))))
                ));
    }

}
