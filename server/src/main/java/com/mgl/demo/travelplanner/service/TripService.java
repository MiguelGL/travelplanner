package com.mgl.demo.travelplanner.service;

import com.mgl.demo.travelplanner.LoggedIn;

import java.time.LocalDate;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.mgl.demo.travelplanner.dao.TripDao;
import com.mgl.demo.travelplanner.entity.Destination;
import com.mgl.demo.travelplanner.entity.Trip;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.service.support.ConflictingValuesException;
import com.mgl.demo.travelplanner.service.support.UnauthorizedUserAccessException;

@Stateless
public class TripService {

    @Inject private TripDao tripDao;

    @Inject private DestinationService destinationService;

    @Inject @LoggedIn private User loggedInUser;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void validateUserTripAccess(User user) {
        if (!loggedInUser.hasAllUserTripsAccess()
                && !user.equals(loggedInUser)) {
            throw new UnauthorizedUserAccessException();
        }
    }

    public Trip createTrip(User user, String destinationName,
            LocalDate startDate, LocalDate endDate,
            String comment) {
        validateUserTripAccess(user);
        Trip.validateDates(startDate, endDate);
        if (tripDao.existsUserOverlappingTrip(user, startDate, endDate)) {
            throw new ConflictingValuesException();
        }
        Destination destination = destinationService.ensureDestinationExists(destinationName);
        Trip trip = new Trip(user, destination, startDate, endDate, comment);
        tripDao.create(trip);
        return trip;
    }

}
