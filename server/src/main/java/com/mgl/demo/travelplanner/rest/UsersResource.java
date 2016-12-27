package com.mgl.demo.travelplanner.rest;

import static com.mgl.demo.travelplanner.rest.support.Pagination.AVAILABLE_RECORDS_COUNT_HEADER;
import static com.mgl.demo.travelplanner.rest.support.Pagination.MAX_PAGINATED_RESULTS;
import static com.mgl.demo.travelplanner.rest.support.Pagination.MAX_PAGINATED_RESULTS_HEADER;
import static com.mgl.demo.travelplanner.rest.support.Pagination.boundLimit;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.mgl.demo.travelplanner.dao.UserDao;
import com.mgl.demo.travelplanner.dao.UserDao.OrderByField;
import com.mgl.demo.travelplanner.entity.Role;
import com.mgl.demo.travelplanner.entity.User;
import com.mgl.demo.travelplanner.rest.support.Pagination.OrderBySpec;
import com.mgl.demo.travelplanner.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jboss.resteasy.annotations.cache.NoCache;

@Stateless
@Path("/sec/users")
@NoCache
@RolesAllowed({Role.ADMINISTRATOR_NAME, Role.MANAGER_NAME})
public class UsersResource {

    @Inject private UserDao userDao;

    @Inject private UserService userService;

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @ToString(callSuper = true)
    public static class UserCreateRequest extends User {

        private static final long serialVersionUID = 1L;

        private String plainPassword;

        @Override
        protected String getEmail() {
            return super.getEmail();
        }

        @Override
        protected String getLastName() {
            return super.getLastName();
        }

        @Override
        protected String getFirstName() {
            return super.getFirstName();
        }

        @Override
        protected Role getRole() {
            return super.getRole();
        }

        @Override
        protected String getPassword() {
            return super.getPassword();
        }

    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(
            @QueryParam("orderBy") @DefaultValue(OrderByField.DEFAULT) OrderByField orderBy,
            @QueryParam("orderSpec") @DefaultValue(OrderBySpec.DEFAULT) OrderBySpec orderSpec,
            @QueryParam("offset") @DefaultValue("0") @Min(0) long offset,
            @QueryParam("limit") @DefaultValue("" + MAX_PAGINATED_RESULTS) @Min(0) long limit) {
        List<User> allUsers = userDao.findAll(orderBy, orderSpec, offset, boundLimit(limit));
        long usersCount = userDao.countAll();
        return Response.ok(new GenericEntity<List<User>>(allUsers) {})
                .header(MAX_PAGINATED_RESULTS_HEADER, MAX_PAGINATED_RESULTS)
                .header(AVAILABLE_RECORDS_COUNT_HEADER, usersCount)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(UserCreateRequest userCreateRequest) {
        return userService.createUser(
                userCreateRequest.getEmail(),
                userCreateRequest.getPlainPassword(),
                userCreateRequest.getRole(),
                userCreateRequest.getFirstName(),
                userCreateRequest.getLastName());
    }

}
