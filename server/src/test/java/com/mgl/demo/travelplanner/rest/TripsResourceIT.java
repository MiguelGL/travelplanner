package com.mgl.demo.travelplanner.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import com.mgl.demo.travelplanner.rest.support.BaseAdminResourceIT;
import com.mgl.demo.travelplanner.rest.support.IntegrationTestsSupport;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

public class TripsResourceIT extends BaseAdminResourceIT {

    @Test
    public void testGetAllUserTrips() {
        IntegrationTestsSupport auxSupport = new IntegrationTestsSupport();
        JsonPath jsonUser = registerUser(auxSupport);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(4);

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", jsonUser.getLong("id"))
                .body(ImmutableMap.of(
                        "destinationName", auxSupport.getTestId() + "-dst-1",
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
                .body("destinationName", is(auxSupport.getTestId() + "-dst-1"))
                .body("startDate", is(toEpochMillis(startDate)))
                .body("endDate", is(toEpochMillis(endDate)))
                .body("comment", is("Have a nice test trip"));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .queryParam("fromDateTs", 0)
                .queryParam("toDateTs", Long.MAX_VALUE)
        .when()
                .get("/sec/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$", not(empty()));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
        .when()
                .get("/sec/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$", not(empty()));

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .queryParam("fromDateTs", 0)
                .queryParam("toDateTs", 0)
        .when()
                .get("/sec/trips")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$", empty());
    }

}
