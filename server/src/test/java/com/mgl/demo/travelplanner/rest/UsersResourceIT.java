package com.mgl.demo.travelplanner.rest;

import org.junit.Test;

import static io.restassured.RestAssured.*;

import javax.ws.rs.core.Response.Status;

import com.mgl.demo.travelplanner.rest.support.BaseAdminResourceIT;
import io.restassured.http.ContentType;

public class UsersResourceIT extends BaseAdminResourceIT {

    @Test
    public void testCreateRegularUser() {
        createUser("REGULAR_USER");
    }

    @Test
    public void testCreateAdministratorUser() {
        createUser("ADMINISTRATOR");
    }

    @Test
    public void testCreateManagerUser() {
        createUser("MANAGER");
    }

    @Test
    public void testListUsers() {
        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
        .when()
                .get("/sec/users")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON);

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .queryParam("orderBy", "EMAIL")
                .queryParam("orderSpec", "ASC")
                .queryParam("offset", 2)
                .queryParam("limit", 10)
        .when()
                .get("/sec/users")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON);

        given()
                .filter(sessionFilter)
                .accept(ContentType.JSON)
                .queryParam("orderBy", "FIRST_NAME")
                .queryParam("orderSpec", "DESC")
                .queryParam("offset", 2)
                .queryParam("limit", 10)
        .when()
                .get("/sec/users")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON);
    }

}
