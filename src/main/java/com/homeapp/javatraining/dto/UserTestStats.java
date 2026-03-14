package com.homeapp.javatraining.dto;

public class UserTestStats extends BaseStats {

    private final String testName;

    public UserTestStats(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

}
