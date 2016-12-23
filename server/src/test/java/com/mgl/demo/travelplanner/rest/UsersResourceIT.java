package com.mgl.demo.travelplanner.rest;

import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import com.mgl.demo.travelplanner.rest.support.BaseResourceIT;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;

public class UsersResourceIT extends BaseResourceIT {

    private SessionFilter sessionFilter;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        sessionFilter = login("admin@email.com", "adminpass");
    }

    @After
    @Override
    public void tearDown() {
        logout(sessionFilter);
        super.tearDown();
    }

    private void createUser(String role) {
        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .body(ImmutableMap.of(
                        "email", support.email(),
                        "plainPassword", support.password(),
                        "role", role,
                        "firstName", support.firstName(),
                        "lastName", support.lastName()
                ))
        .when()
                .post("/sec/users")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON);

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .body(ImmutableMap.of(
                        "email", support.email(),
                        "plainPassword", support.password() + "-other",
                        "role", role,
                        "firstName", support.firstName() + "-other",
                        "lastName", support.lastName() + "-other"
                ))
        .when()
                .post("/sec/users")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());
    }

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

}
