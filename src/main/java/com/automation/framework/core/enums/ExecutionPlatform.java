package com.automation.framework.core.enums;

/**
 * Enumeration of supported execution platforms.
 */
public enum ExecutionPlatform {
    LOCAL("local"),
    GRID("grid"),
    BROWSERSTACK("browserstack"),
    SAUCELABS("saucelabs");

    private final String platformName;

    ExecutionPlatform(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public static ExecutionPlatform fromString(String platform) {
        for (ExecutionPlatform type : ExecutionPlatform.values()) {
            if (type.platformName.equalsIgnoreCase(platform)) {
                return type;
            }
        }
        return LOCAL; // Default to local
    }
}
