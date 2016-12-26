package com.mgl.demo.travelplanner.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import java.time.LocalDate;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import com.mgl.demo.travelplanner.rest.support.BaseResourceIT;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.After;
import org.junit.Before;

public class TripResourceIT extends BaseResourceIT {

    private JsonPath jsonUser;
    private SessionFilter sessionFilter;

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
    public void testCreateAndEditTrip() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(4);

        JsonPath jsonTrip = given()
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
                .body("id", isA(Number.class))
                .body("userEmail", is(jsonUser.getString("email")))
                .body("destinationName", is(support.getTestId() + "-dst-1"))
                .body("startDate", is(toEpochMillis(startDate)))
                .body("endDate", is(toEpochMillis(endDate)))
                .body("comment", is("Have a nice test trip"))
        .extract()
                .jsonPath();

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("tripId", jsonTrip.getLong("id"))
        .when()
                .get("/sec/trips/{tripId}")
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
                .pathParam("tripId", jsonTrip.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", support.getTestId() + "-dst-1-update",
                        "startDate", toEpochMillis(startDate.minusDays(1)),
                        "endDate", toEpochMillis(endDate.plusDays(1)),
                        "comment", "Have a nice test trip, updated"
                ))
        .when()
                .put("/sec/trips/{tripId}")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("id", isA(Number.class))
                .body("userEmail", is(jsonUser.getString("email")))
                .body("destinationName", is(support.getTestId() + "-dst-1-update"))
                .body("startDate", is(toEpochMillis(startDate.minusDays(1))))
                .body("endDate", is(toEpochMillis(endDate.plusDays(1))))
                .body("comment", is("Have a nice test trip, updated"));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("tripId", jsonTrip.getLong("id"))
        .when()
                .delete("/sec/trips/{tripId}")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("id", isA(Number.class))
                .body("userEmail", is(jsonUser.getString("email")))
                .body("destinationName", is(support.getTestId() + "-dst-1-update"))
                .body("startDate", is(toEpochMillis(startDate.minusDays(1))))
                .body("endDate", is(toEpochMillis(endDate.plusDays(1))))
                .body("comment", is("Have a nice test trip, updated"));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("tripId", jsonTrip.getLong("id"))
        .when()
                .get("/sec/trips/{tripId}")
        .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

}
