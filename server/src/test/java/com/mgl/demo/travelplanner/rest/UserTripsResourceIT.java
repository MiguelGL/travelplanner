package com.mgl.demo.travelplanner.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import com.mgl.demo.travelplanner.rest.support.BaseAdminResourceIT;
import com.mgl.demo.travelplanner.rest.support.BaseResourceIT;
import com.mgl.demo.travelplanner.rest.support.IntegrationTestsSupport;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.After;
import org.junit.Before;

public class UserTripsResourceIT extends BaseResourceIT {

    private JsonPath jsonUser;
    private SessionFilter sessionFilter;

    private static JsonPath registerUser(IntegrationTestsSupport support) {
        return given()
                .contentType(ContentType.URLENC)
                .formParam("email", support.email())
                .formParam("password", support.password())
                .formParam("firstName", support.firstName())
                .formParam("lastName", "")
                .accept(ContentType.JSON)
        .when()
                .post("/login/register")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("id", isA(Number.class))
                .body("updated", notNullValue())
                .body("email", is(support.email()))
                .body("password", nullValue())
                .body("firstName", is(support.firstName()))
                .body("lastName", is(""))
        .extract()
                .jsonPath();
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        jsonUser = registerUser(support);
        sessionFilter = login(support.email(), support.password());
    }

    @After
    @Override
    public void tearDown() {
        if (sessionFilter != null) {
            logout(sessionFilter);
        }
        super.tearDown();
    }

    @Test
    public void testAdministratorUserTripsAccess() {
        SessionFilter auxSessionFilter = BaseAdminResourceIT.adminLogin();

        given()
                .filter(auxSessionFilter)
                .accept(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
        .when()
                .get("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$", empty());

        logout(auxSessionFilter);
    }

    @Test
    public void testUnauthorizedUserTripsAccess() {
        IntegrationTestsSupport auxSupport = new IntegrationTestsSupport();
        JsonPath auxJsonUser = registerUser(auxSupport);
        SessionFilter auxSessionFilter = login(auxSupport.email(), auxSupport.password());

        given()
                .filter(auxSessionFilter)
                .accept(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
        .when()
                .get("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.FORBIDDEN.getStatusCode());

        logout(auxSessionFilter);

    }

    @Test
    public void testListUserTripsEmpty() {
        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
        .when()
                .get("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$", empty());
    }

    private long toEpochMillis(LocalDate localDate) {
        return localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Test
    public void testCreateUserTrips() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(4);

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-1",
                        "startDate", toEpochMillis(startDate),
                        "endDate", toEpochMillis(endDate),
                        "comment", "Have a nice test trip"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("userEmail", is(jsonUser.getString("email")))
                .body("destinationName", is(support.getTestId() + "-dst-1"))
                .body("startDate", is(toEpochMillis(startDate)))
                .body("endDate", is(toEpochMillis(endDate)))
                .body("comment", is("Have a nice test trip"));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-2",
                        "startDate", toEpochMillis(startDate),
                        "endDate", toEpochMillis(endDate),
                        "comment", "Have a nice test trip again"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-3",
                        "startDate", toEpochMillis(startDate.minusDays(14)),
                        "endDate", toEpochMillis(startDate.plusDays(1)),
                        "comment", "Have a nice test trip again"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-4",
                        "startDate", toEpochMillis(endDate.minusDays(1)),
                        "endDate", toEpochMillis(endDate.plusDays(5)),
                        "comment", "Have a nice test trip again"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-4",
                        "startDate", toEpochMillis(startDate.plusDays(1)),
                        "endDate", toEpochMillis(endDate.minusDays(1)),
                        "comment", "Have a nice test trip again"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-4",
                        "startDate", toEpochMillis(startDate.minusDays(1)),
                        "endDate", toEpochMillis(endDate.plusDays(1)),
                        "comment", "Have a nice test trip again"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-1",
                        "startDate", toEpochMillis(startDate.plusMonths(1)),
                        "endDate", toEpochMillis(endDate.plusMonths(1)),
                        "comment", "Have a nice test trip"
                ))
        .when()
                .post("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("userEmail", is(jsonUser.getString("email")))
                .body("destinationName", is(support.getTestId() + "-dst-1"))
                .body("startDate", is(toEpochMillis(startDate.plusMonths(1))))
                .body("endDate", is(toEpochMillis(endDate.plusMonths(1))))
                .body("comment", is("Have a nice test trip"));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
        .when()
                .get("/sec/users/{userId}/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$", not(empty()));
    }

}
