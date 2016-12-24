package com.mgl.demo.travelplanner.rest.support;

import java.util.Random;

import lombok.Data;

@Data
public class IntegrationTestsSupport {

    private static final Random RND = new Random();

    private final int testId;
    private final String testIdStr;

    public IntegrationTestsSupport() {
        this.testId = RND.nextInt(1_000_000);
        this.testIdStr = String.valueOf(testId);
    }

    public String email() {
        return testIdStr + "@email.com";
    }

    public String password() {
        return testIdStr + "-secret";
    }

    public String firstName() {
        return "First-" + testIdStr;
    }

    public String lastName() {
        return "Last-" + testIdStr;
    }

}
