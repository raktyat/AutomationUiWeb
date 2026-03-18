package com.automation.framework.core.enums;

/**
 * Enumeration of supported platforms for test execution.
 */
public enum PlatformType {
    WEB("web"),
    ANDROID("android"),
    IOS("ios"),
    API("api");

    private final String platformName;

    PlatformType(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public static PlatformType fromString(String platform) {
        for (PlatformType type : PlatformType.values()) {
            if (type.platformName.equalsIgnoreCase(platform)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported platform: " + platform);
    }
}
