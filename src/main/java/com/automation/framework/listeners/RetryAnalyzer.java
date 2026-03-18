package com.automation.framework.listeners;

import com.automation.framework.config.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry Analyzer for automatically retrying failed tests.
 */
@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private final int maxRetryCount;

    public RetryAnalyzer() {
        this.maxRetryCount = ConfigurationManager.getInstance()
                .getIntProperty("retry.count");
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            log.info("Retrying test {} - Attempt {} of {}",
                    result.getMethod().getMethodName(),
                    retryCount,
                    maxRetryCount);
            return true;
        }
        return false;
    }
}
