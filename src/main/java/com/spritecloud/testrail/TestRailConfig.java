package com.spritecloud.testrail;

import com.spritecloud.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for TestRail integration.
 * Manages TestRail connection settings and project details.
 */
public class TestRailConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestRailConfig.class);
    private final ConfigurationManager config;

    private final String url;
    private final String username;
    private final String apiKey;
    private final int projectId;
    private final boolean enabled;

    public TestRailConfig() {
        this.config = ConfigurationManager.getInstance();
        this.url = config.getTestRailUrl();
        this.username = config.getTestRailUsername();
        this.apiKey = config.getTestRailApiKey();
        this.projectId = config.getTestRailProjectId();
        this.enabled = config.isTestRailEnabled();

        if (enabled) {
            logger.info("TestRail integration is ENABLED");
            logger.info("TestRail URL: {}", url);
            logger.info("TestRail Project ID: {}", projectId);
            logger.info("TestRail Username: {}", username);
        } else {
            logger.info("TestRail integration is DISABLED");
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getProjectId() {
        return projectId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Validates that all required TestRail configuration is present
     */
    public void validate() {
        if (!enabled) {
            return;
        }

        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("TestRail URL is not configured");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("TestRail username is not configured");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("TestRail API key is not configured");
        }
        if (projectId <= 0) {
            throw new IllegalStateException("TestRail project ID is not configured");
        }

        logger.info("TestRail configuration validated successfully");
    }
}
