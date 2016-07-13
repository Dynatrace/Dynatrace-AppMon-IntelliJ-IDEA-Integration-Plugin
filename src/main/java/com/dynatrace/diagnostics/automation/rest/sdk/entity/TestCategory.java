package com.dynatrace.diagnostics.automation.rest.sdk.entity;

/**
 * Created by krzysztof.necel on 2016-04-04.
 */
public enum TestCategory {

    UNIT("unit"),
    UI_DRIVEN("uidriven"),
    PERFORMANCE("performance"),
    WEB_API("webapi");

    private final String id;

    TestCategory(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static TestCategory fromString(String id) {
        for (TestCategory testCategory : TestCategory.values()) {
            if (testCategory.id.equalsIgnoreCase(id)) {
                return testCategory;
            }
        }
        throw new IllegalArgumentException(String.format("Unrecognized test category: %s", id));
    }
}
