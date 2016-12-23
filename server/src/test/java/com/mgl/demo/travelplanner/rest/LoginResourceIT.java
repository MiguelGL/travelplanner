package com.mgl.demo.travelplanner.rest;

import static io.restassured.RestAssured.*;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.core.Response.Status;

import com.mgl.demo.travelplanner.rest.support.IntegrationTestsSupport;
import io.restassured.RestAssured;
import io.restassured.config.SessionConfig;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoginResourceIT {

    private static IntegrationTestsSupport support;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "INFO");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");

        RestAssured.config = newConfig().sessionConfig(
                new SessionConfig().sessionIdName("TAVEL-PLANNER-SESSION-ID"));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/travelplanner/api";
        support = new IntegrationTestsSupport();
    }

    @AfterClass
    public static void tearDownClass() {
        support = null;
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
