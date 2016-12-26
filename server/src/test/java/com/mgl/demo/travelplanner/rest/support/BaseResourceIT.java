package com.mgl.demo.travelplanner.rest.support;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.ws.rs.core.Response.Status;

import com.google.common.base.Charsets;
import io.restassured.RestAssured;
import io.restassured.config.SessionConfig;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class BaseResourceIT {

    protected IntegrationTestsSupport support;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "INFO");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");

        RestAssured.config = newConfig().sessionConfig(
                new SessionConfig().sessionIdName("TAVEL-PLANNER-SESSION-ID"));
        RestAssured.config.getEncoderConfig().defaultContentCharset(Charsets.UTF_8.name());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/travelplanner/api";
    }

    @Before
    public void setUp() {
        support = new IntegrationTestsSupport();
    }

    @After
    public void tearDown() {
        support = null;
    }

    protected long toEpochMillis(LocalDate localDate) {
        return localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    protected static JsonPath registerUser(IntegrationTestsSupport support) {
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

    protected static void logout(SessionFilter sessionFilter) {
        given()
                .filter(sessionFilter)
        .when()
                .delete("/login")
        .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    protected static SessionFilter login(String email, String password) {
        SessionFilter sessionFilter = new SessionFilter();

        given()
                .formParam("email", email)
                .formParam("password", password)
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
                .body("email", is(email))
                .body("password", nullValue());

        return sessionFilter;
    }

}
