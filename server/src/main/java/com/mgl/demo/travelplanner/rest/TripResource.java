package com.mgl.demo.travelplanner.rest;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.mgl.demo.travelplanner.dao.TripDao;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.Trip;
import com.mgl.demo.travelplanner.rest.UserTripsResource.TripCreateRequest;
import com.mgl.demo.travelplanner.service.DestinationService;
import com.mgl.demo.travelplanner.service.TripService;
import org.jboss.resteasy.annotations.cache.NoCache;

@Path("/sec/trips/{tripId}")
@Stateless
@NoCache
@RolesAllowed({Role.ADMINISTRATOR_NAME, Role.REGULAR_USER_NAME, Role.MANAGER_NAME})
public class TripResource {

    @Inject private TripDao tripDao;

    @Inject private DestinationService destinationService;
    @Inject private TripService tripService;

    @GET
    public Trip getTrip(@PathParam("tripId") Long tripId) {
        Trip trip = tripDao.findById(tripId);
        return tripService.validateUserTripAccess(trip);
    }

    @PUT
    public Trip updateTrip(@PathParam("tripId") Long tripId, TripCreateRequest tripCreateRequest) {
        Trip trip = tripDao.findById(tripId);
        Trip tripTemplate = trip.newWithNullables(
                tripCreateRequest.maybeGetDestinationName()
                        .map(destinationService::ensureDestinationExists)
                        .orElse(null),
                tripCreateRequest.getStartDate(),
                tripCreateRequest.getEndDate(),
                tripCreateRequest.getComment());
        return tripService.updateTrip(trip, tripTemplate);
    }

    @DELETE
    public Trip deleteTrip(@PathParam("tripId") Long tripId) {
        Trip trip = tripDao.findById(tripId);
        tripDao.delete(tripService.validateUserTripAccess(trip));
        return trip;
    }

}
