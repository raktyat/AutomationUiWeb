package com.automation.framework.core.enums;

/**
 * Enumeration of supported browser types for web testing.
 */
public enum BrowserType {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    SAFARI("safari");

    private final String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserName() {
        return browserName;
    }

    public static BrowserType fromString(String browser) {
        for (BrowserType type : BrowserType.values()) {
            if (type.browserName.equalsIgnoreCase(browser)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported browser: " + browser);
    }
}
