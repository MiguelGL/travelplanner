package com.mgl.demo.travelplanner.rest;

import static com.mgl.demo.travelplanner.entity.Destination.DESTINATION_MAX_LEN;
import static com.mgl.demo.travelplanner.entity.Destination.DESTINATION_MIX_LEN;
import static com.mgl.demo.travelplanner.entity.Trip.COMMENT_MAX_LEN;
import static com.mgl.demo.travelplanner.rest.support.Pagination.AVAILABLE_RECORDS_COUNT_HEADER;
import static com.mgl.demo.travelplanner.rest.support.Pagination.MAX_PAGINATED_RESULTS;
import static com.mgl.demo.travelplanner.rest.support.Pagination.MAX_PAGINATED_RESULTS_HEADER;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.mgl.demo.travelplanner.dao.TripDao;
import com.mgl.demo.travelplanner.dao.TripDao.OrderByField;
import com.mgl.demo.travelplanner.dao.UserDao;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.Trip;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.entity.support.LocalDateAdapter;
import com.mgl.demo.travelplanner.rest.support.Pagination.OrderBySpec;
import com.mgl.demo.travelplanner.service.TripService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.jboss.resteasy.annotations.cache.NoCache;

@Stateless
@Path("/sec/users/{userId}/trips")
@NoCache
@RolesAllowed({Role.REGULAR_USER_NAME, Role.ADMINISTRATOR_NAME})
public class UserTripsResource {

    @Inject private UserDao userDao;
    @Inject private TripDao tripDao;

    @Inject private TripService tripService;

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @ToString(callSuper = true)
    public static class TripCreateRequest {

        @NotNull
        @NotBlank
        @Size(min = DESTINATION_MIX_LEN, max = DESTINATION_MAX_LEN)
        private String destinationName;

        @NotNull
        @XmlJavaTypeAdapter(LocalDateAdapter.class)
        private LocalDate startDate;
        
        @NotNull
        @XmlJavaTypeAdapter(LocalDateAdapter.class)
        private LocalDate endDate;

        @Size(max = COMMENT_MAX_LEN)
        private String comment;

        public Optional<String> maybeGetDestinationName() {
            return Optional.ofNullable(getDestinationName());
        }

    }

    public static LocalDate fromTs(long ts) {
        return Instant.ofEpochMilli(ts).atZone(ZoneOffset.UTC).toLocalDate();
    }

    @GET
    public Response getUserTrips(
            @PathParam("userId") Long userId,
            @QueryParam("fromDateTs") Long fromDateTs,
            @QueryParam("toDateTs") Long toDateTs,
            @QueryParam("orderBy") @DefaultValue(OrderByField.DEFAULT) OrderByField orderBy,
            @QueryParam("orderSpec") @DefaultValue(OrderBySpec.DEFAULT) OrderBySpec orderSpec,
            @QueryParam("offset") @DefaultValue("0") @Min(0) long offset,
            @QueryParam("limit") @DefaultValue("" + MAX_PAGINATED_RESULTS) @Min(0) long limit) {
        User user = userDao.findById(userId);
        tripService.validateUserTripAccess(user);
        Optional<LocalDate> maybeFromDate = Optional.ofNullable(fromDateTs).map(UserTripsResource::fromTs);
        Optional<LocalDate> maybeToDate = Optional.ofNullable(toDateTs).map(UserTripsResource::fromTs);
        List<Trip> trips = tripDao.findUserTrips(
                user, maybeFromDate, maybeToDate, orderBy, orderSpec, offset, limit);
        long tripsCount = tripDao.countUserTrips(user, maybeFromDate, maybeToDate);
        return Response.ok(new GenericEntity<List<Trip>>(trips) {})
                .header(MAX_PAGINATED_RESULTS_HEADER, MAX_PAGINATED_RESULTS)
                .header(AVAILABLE_RECORDS_COUNT_HEADER, tripsCount)
                .build();
    }

    @POST
    public Trip createUserTrip(
            @PathParam("userId") Long userId,
            @Valid TripCreateRequest tripCreateRequest) {
        User user = userDao.findById(userId);
        return tripService.createTrip(
                user,
                tripCreateRequest.getDestinationName(),
                tripCreateRequest.getStartDate(),
                tripCreateRequest.getEndDate(),
                Trip.ensureComment(tripCreateRequest.getComment()));
    }

}
