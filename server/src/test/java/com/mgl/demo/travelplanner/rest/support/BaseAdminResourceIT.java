package com.mgl.demo.travelplanner.rest.support;

import static io.restassured.RestAssured.*;

import static com.mgl.demo.travelplanner.rest.support.BaseResourceIT.login;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.After;
import org.junit.Before;

public class BaseAdminResourceIT extends BaseResourceIT {

    protected SessionFilter sessionFilter;

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

    protected JsonPath createUser(String role) {
        JsonPath userJson = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .body(ImmutableMap.of(
                        "email", support.email(),
                        "plainPassword", support.password(),
                        "role", role,
                        "firstName", support.firstName(),
                        "lastName", support.lastName()))
        .when()
                .post("/sec/users")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
        .extract()
                .jsonPath();

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .body(ImmutableMap.of(
                        "email", support.email(),
                        "plainPassword", support.password() + "-other",
                        "role", role,
                        "firstName", support.firstName() + "-other",
                        "lastName", support.lastName() + "-other"))
        .when()
                .post("/sec/users")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());

        return userJson;
    }

}
