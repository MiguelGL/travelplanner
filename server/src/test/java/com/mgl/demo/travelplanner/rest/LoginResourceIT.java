package com.mgl.demo.travelplanner.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.core.Response.Status;

import com.mgl.demo.travelplanner.rest.support.BaseResourceIT;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import org.junit.Test;

public class LoginResourceIT extends BaseResourceIT {

    @Test
    public void testDuplicateRegistration() {
        given()
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
                .body("lastName", is(""));

        given()
                .contentType(ContentType.URLENC)
                .formParam("email", support.email())
                .formParam("password", support.password() + "-other")
                .formParam("firstName", support.firstName() + "-other")
                .formParam("lastName", "")
                .accept(ContentType.JSON)
        .when()
                .post("/login/register")
        .then()
                .statusCode(Status.CONFLICT.getStatusCode());
    }

    @Test
    public void testInvalidRegistrations() {
        given()
                .contentType(ContentType.URLENC)
                .formParam("email", support.email())
                .formParam("password", "")
                .formParam("firstName", support.firstName())
                .formParam("lastName", "")
                .accept(ContentType.JSON)
        .when()
                .post("/login/register")
        .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        given()
                .contentType(ContentType.URLENC)
                .formParam("email", "")
                .formParam("password", support.password())
                .formParam("firstName", support.firstName())
                .formParam("lastName", "")
                .accept(ContentType.JSON)
        .when()
                .post("/login/register")
        .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        given()
                .contentType(ContentType.URLENC)
                .formParam("email", support.email())
                .formParam("password", support.password())
                .formParam("firstName", "")
                .formParam("lastName", "")
                .accept(ContentType.JSON)
        .when()
                .post("/login/register")
        .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testValidRegistrationAndLogin() {
        given()
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
                .body("lastName", is(""));

        SessionFilter sessionFilter = new SessionFilter();

        given()
                .formParam("email", support.email())
                .formParam("password", support.password())
                .accept(ContentType.JSON)
                .filter(sessionFilter)
        .when()
                .post("/login")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .cookie("TAVEL-PLANNER-SESSION-ID")
                .body("id", isA(Number.class))
                .body("updated", notNullValue())
                .body("email", is(support.email()))
                .body("password", nullValue())
                .body("firstName", is(support.firstName()))
                .body("lastName", is(""));

        given()
                .accept(ContentType.JSON)
                .filter(sessionFilter)
        .when()
                .get("/login")
        .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("id", isA(Number.class))
                .body("updated", notNullValue())
                .body("email", is(support.email()))
                .body("password", nullValue())
                .body("firstName", is(support.firstName()))
                .body("lastName", is(""));

        given()
                .filter(sessionFilter)
        .when()
                .delete("/login")
        .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());

        given()
                .filter(sessionFilter)
        .when()
                .delete("/login")
        .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());

        given()
                .accept(ContentType.JSON)
                .filter(sessionFilter)
        .when()
                .get("/login")
        .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());
    }

}
