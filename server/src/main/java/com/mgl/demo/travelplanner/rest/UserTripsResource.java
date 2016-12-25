package com.mgl.demo.travelplanner.rest;

import static com.mgl.demo.travelplanner.entity.Destination.DESTINATION_MAX_LEN;
import static com.mgl.demo.travelplanner.entity.Destination.DESTINATION_MIX_LEN;
import static com.mgl.demo.travelplanner.entity.Trip.COMMENT_MAX_LEN;
import static com.mgl.demo.travelplanner.rest.Pagination.MAX_PAGINATED_RESULTS;

import java.time.LocalDate;
import java.util.List;

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
import com.mgl.demo.travelplanner.rest.Pagination.OrderBySpec;
import com.mgl.demo.travelplanner.service.TripService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Stateless
@Path("/sec/users/{userId}/trips")
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

    }

    @GET
    public GenericEntity<List<Trip>> getUserTrips(
            @PathParam("userId") Long userId,
            @QueryParam("orderBy") @DefaultValue(OrderByField.DEFAULT) OrderByField orderBy,
            @QueryParam("orderSpec") @DefaultValue(OrderBySpec.DEFAULT) OrderBySpec orderSpec,
            @QueryParam("offset") @DefaultValue("0") @Min(0) long offset,
            @QueryParam("limit") @DefaultValue("" + MAX_PAGINATED_RESULTS) @Min(0) long limit) {
        User user = userDao.findById(userId);
        tripService.validateUserTripAccess(user);
        List<Trip> trips = tripDao.findUserTrips(user, orderBy, orderSpec, offset, limit);
        return new GenericEntity<List<Trip>>(trips) {};
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
