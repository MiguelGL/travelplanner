package com.mgl.demo.travelplanner.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mgl.demo.travelplanner.dao.support.BaseEntityDao;
import com.mgl.demo.travelplanner.entity.QTrip;
import com.mgl.demo.travelplanner.entity.Trip;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.rest.support.Pagination.OrderBySpec;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    public long countUserTrips(User user,
            Optional<LocalDate> maybeFromDate, Optional<LocalDate> maybeToDate) {
        BooleanExpression predicate = pathBase().user.eq(user);
        predicate = predicate.and(
                maybeFromDate.map(fromDate -> pathBase().startDate.before(fromDate).not())
                .orElse(null));
        predicate = predicate.and(
                maybeToDate.map(toDate -> pathBase().endDate.after(toDate).not()
                                          .and(pathBase().endDate.eq(toDate).not()))
                .orElse(null));
        return count(predicate);
    }

    public List<Trip> findUserTrips(User user,
            Optional<LocalDate> maybeFromDate, Optional<LocalDate> maybeToDate,
            OrderByField orderByField, OrderBySpec orderBySpec,
            long offset, long limit) {
        BooleanExpression predicate = pathBase().user.eq(user);
        predicate = predicate.and(
                maybeFromDate.map(fromDate -> pathBase().startDate.before(fromDate).not())
                .orElse(null));
        predicate = predicate.and(
                maybeToDate.map(toDate -> pathBase().endDate.after(toDate).not()
                                          .and(pathBase().endDate.eq(toDate).not()))
                .orElse(null));
        return find(
                Optional.of(offset),
                Optional.of(limit),
                orderByField.maybeBuildOrderSpecifier(orderBySpec),
                predicate);
    }

    public long countAllTrips(Optional<LocalDate> maybeFromDate, Optional<LocalDate> maybeToDate) {
        Optional<BooleanExpression> maybeFromPredicate =
                maybeFromDate.map(fromDate -> pathBase().startDate.before(fromDate).not());

        Optional<BooleanExpression> maybeFullPredicate = maybeToDate
                .map(toDate -> pathBase().endDate.after(toDate).not()
                               .and(pathBase().endDate.eq(toDate).not()))
                .flatMap(p2 -> maybeFromPredicate.map(p1 -> p1.and(p2)));

        return maybeFullPredicate
                .map(predicate -> count(predicate))
                .orElseGet(() -> count());
    }

    public List<Trip> findAllTrips(
            Optional<LocalDate> maybeFromDate, Optional<LocalDate> maybeToDate,
            OrderByField orderByField, OrderBySpec orderBySpec,
            long offset, long limit) {
        Optional<BooleanExpression> maybeFromPredicate =
                maybeFromDate.map(fromDate -> pathBase().startDate.before(fromDate).not());

        Optional<BooleanExpression> maybeFullPredicate = maybeToDate
                .map(toDate -> pathBase().endDate.after(toDate).not()
                               .and(pathBase().endDate.eq(toDate).not()))
                .flatMap(p2 -> maybeFromPredicate.map(p1 -> p1.and(p2)));

        return maybeFullPredicate
                .map(predicate -> find(
                    Optional.of(offset),
                    Optional.of(limit),
                    orderByField.maybeBuildOrderSpecifier(orderBySpec),
                    predicate))
                .orElseGet(() -> find(
                    Optional.of(offset),
                    Optional.of(limit),
                    orderByField.maybeBuildOrderSpecifier(orderBySpec)));
    }

    public boolean existsOverlappingUserTrip(User user,
            LocalDate startDate, LocalDate endDate) {
        return existsOverlappingUserTrip(user, startDate, endDate, ImmutableSet.of());
    }

    public boolean existsOverlappingUserTrip(User user,
            LocalDate startDate, LocalDate endDate,
            Set<Trip> excludedTrips) {
        return existsAccordingTo(
                pathBase().user.eq(user)
                .and(pathBase().notIn(excludedTrips))
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
