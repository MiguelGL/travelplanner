package com.mgl.demo.travelplanner.rest;

import static com.mgl.demo.travelplanner.rest.support.Pagination.AVAILABLE_RECORDS_COUNT_HEADER;
import static com.mgl.demo.travelplanner.rest.support.Pagination.MAX_PAGINATED_RESULTS;
import static com.mgl.demo.travelplanner.rest.support.Pagination.MAX_PAGINATED_RESULTS_HEADER;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import com.mgl.demo.travelplanner.dao.TripDao;
import com.mgl.demo.travelplanner.dao.TripDao.OrderByField;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.Trip;
import com.mgl.demo.travelplanner.rest.support.Pagination.OrderBySpec;
import org.jboss.resteasy.annotations.cache.NoCache;

@Stateless
@Path("/sec/trips")
@NoCache
@RolesAllowed({Role.ADMINISTRATOR_NAME})
public class TripsResource {

    @Inject private TripDao tripDao;

    @GET
    public Response getUserTrips(
            @QueryParam("fromDateTs") Long fromDateTs,
            @QueryParam("toDateTs") Long toDateTs,
            @QueryParam("orderBy") @DefaultValue(OrderByField.DEFAULT) OrderByField orderBy,
            @QueryParam("orderSpec") @DefaultValue(OrderBySpec.DEFAULT) OrderBySpec orderSpec,
            @QueryParam("offset") @DefaultValue("0") @Min(0) long offset,
            @QueryParam("limit") @DefaultValue("" + MAX_PAGINATED_RESULTS) @Min(0) long limit) {
        Optional<LocalDate> maybeFromDate = Optional.ofNullable(fromDateTs).map(UserTripsResource::fromTs);
        Optional<LocalDate> maybeToDate = Optional.ofNullable(toDateTs).map(UserTripsResource::fromTs);
        List<Trip> trips = tripDao.findAllTrips(
                maybeFromDate, maybeToDate, orderBy, orderSpec, offset, limit);
        long tripsCount = tripDao.countAllTrips(maybeFromDate, maybeToDate);
        return Response.ok(new GenericEntity<List<Trip>>(trips) {})
                .header(MAX_PAGINATED_RESULTS_HEADER, MAX_PAGINATED_RESULTS)
                .header(AVAILABLE_RECORDS_COUNT_HEADER, tripsCount)
                .build();
    }

}
