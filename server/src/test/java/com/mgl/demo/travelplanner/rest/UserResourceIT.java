package com.mgl.demo.travelplanner.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import com.mgl.demo.travelplanner.entity.Role;
import org.junit.Test;

import com.mgl.demo.travelplanner.rest.support.BaseAdminResourceIT;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

public class UserResourceIT extends BaseAdminResourceIT {

    @Test
    public void testCrudUser() {
        JsonPath userJson = createUser(Role.REGULAR_USER_NAME);

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("userId", 0)
        .when()
                .get("/sec/users/{userId}")
        .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .pathParam("userId", userJson.getLong("id"))
        .when()
                .get("/sec/users/{userId}")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON);

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("userId", userJson.getLong("id"))
                .body(ImmutableMap.of(
                        "email", "new-" + support.email(),
                        "plainPassword", "new-" + support.password(),
                        "role", Role.ADMINISTRATOR_NAME,
                        "firstName", "new-" + support.firstName(),
                        "lastName", "new-" + support.lastName()))
        .when()
                .put("/sec/users/{userId}")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("email", is(support.email()))
                .body("role", is(Role.REGULAR_USER_NAME))
                .body("firstName", is("new-" + support.firstName()))
                .body("lastName", is("new-" + support.lastName()));

        SessionFilter auxSessionFilter = login(support.email(), "new-" + support.password());
        logout(auxSessionFilter);

        given()
                .accept(ContentType.JSON)
                .queryParam("userId", userJson.getLong("id"))
        .when()
                .delete("/sec/users/{userId}")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON);

        given()
                .accept(ContentType.JSON)
                .queryParam("userId", userJson.getLong("id"))
        .when()
                .delete("/sec/users/{userId}")
        .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

}
