package com.spritecloud.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized configuration manager for environment-based settings.
 */
public class ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final Dotenv dotenv;

    // Configuration Keys
    private static final String API_BASE_URL_KEY = "API_BASE_URL";
    private static final String UI_BASE_URL_KEY = "UI_BASE_URL";
    private static final String UI_TEST_USERNAME_KEY = "UI_TEST_USERNAME";
    private static final String UI_TEST_PASSWORD_KEY = "UI_TEST_PASSWORD";
    private static final String BROWSER_KEY = "BROWSER";
    private static final String HEADLESS_KEY = "HEADLESS";
    private static final String TIMEOUT_KEY = "TIMEOUT";
    private static final String ENVIRONMENT_KEY = "ENVIRONMENT";
    private static final String LOG_LEVEL_KEY = "LOG_LEVEL";
    private static final String MOCK_API_KEY = "MOCK_API";

    // TestRail Configuration Keys
    private static final String TESTRAIL_ENABLED_KEY = "TESTRAIL_ENABLED";
    private static final String TESTRAIL_URL_KEY = "TESTRAIL_URL";
    private static final String TESTRAIL_USERNAME_KEY = "TESTRAIL_USERNAME";
    private static final String TESTRAIL_API_KEY_KEY = "TESTRAIL_API_KEY";
    private static final String TESTRAIL_PROJECT_ID_KEY = "TESTRAIL_PROJECT_ID";

    // Default Values
    private static final String DEFAULT_API_BASE_URL = "https://fakestoreapi.com";
    private static final String DEFAULT_UI_BASE_URL = "https://www.saucedemo.com";
    private static final String DEFAULT_BROWSER = "chromium";
    private static final String DEFAULT_TIMEOUT = "30000";
    private static final String DEFAULT_ENVIRONMENT = "test";
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final String DEFAULT_MOCK_API = "false";

    // TestRail Default Values
    private static final String DEFAULT_TESTRAIL_ENABLED = "false";
    private static final String DEFAULT_TESTRAIL_URL = "";
    private static final String DEFAULT_TESTRAIL_PROJECT_ID = "0";

    private ConfigurationManager() {
        try {
            // Try to load .env file, but don't fail if it doesn't exist (CI environment)
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            logger.info("Configuration loaded successfully for environment: {}", getEnvironment());
        } catch (Exception e) {
            logger.warn("Failed to load .env file, falling back to system environment variables", e);
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }

    /**
     * Gets singleton instance of ConfigurationManager
     * @return ConfigurationManager instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Gets configuration value with fallback to default
     * @param key Configuration key
     * @param defaultValue Default value if key not found
     * @return Configuration value
     */
    private String getConfigValue(String key, String defaultValue) {
        // Priority: System property > .env file > default value
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }

        String envValue = dotenv.get(key);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        logger.debug("Using default value for key: {}", key);
        return defaultValue;
    }

    // API Configuration
    public String getApiBaseUrl() {
        return getConfigValue(API_BASE_URL_KEY, DEFAULT_API_BASE_URL);
    }

    // UI Configuration
    public String getUiBaseUrl() {
        return getConfigValue(UI_BASE_URL_KEY, DEFAULT_UI_BASE_URL);
    }

    public String getUiTestUsername() {
        return getConfigValue(UI_TEST_USERNAME_KEY, "standard_user");
    }

    public String getUiTestPassword() {
        return getConfigValue(UI_TEST_PASSWORD_KEY, "secret_sauce");
    }

    // Browser Configuration
    public String getBrowser() {
        return getConfigValue(BROWSER_KEY, DEFAULT_BROWSER);
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getConfigValue(HEADLESS_KEY, "true"));
    }

    public int getTimeout() {
        return Integer.parseInt(getConfigValue(TIMEOUT_KEY, DEFAULT_TIMEOUT));
    }

    // Environment Configuration
    public String getEnvironment() {
        return getConfigValue(ENVIRONMENT_KEY, DEFAULT_ENVIRONMENT);
    }

    public String getLogLevel() {
        return getConfigValue(LOG_LEVEL_KEY, DEFAULT_LOG_LEVEL);
    }

    // Mock API Configuration
    /**
     * Check if mock API mode is enabled
     * Used in CI/CD environments where real API is blocked by Cloudflare
     * @return true if MOCK_API environment variable is set to true
     */
    public boolean isMockApiEnabled() {
        return Boolean.parseBoolean(getConfigValue(MOCK_API_KEY, DEFAULT_MOCK_API));
    }

    /**
     * Get the mock API server URL
     * @return mock server base URL (localhost:8089)
     */
    public String getMockApiUrl() {
        return "http://localhost:8089";
    }

    // TestRail Configuration
    /**
     * Check if TestRail integration is enabled
     * @return true if TESTRAIL_ENABLED environment variable is set to true
     */
    public boolean isTestRailEnabled() {
        return Boolean.parseBoolean(getConfigValue(TESTRAIL_ENABLED_KEY, DEFAULT_TESTRAIL_ENABLED));
    }

    /**
     * Get TestRail URL
     * @return TestRail instance URL
     */
    public String getTestRailUrl() {
        return getConfigValue(TESTRAIL_URL_KEY, DEFAULT_TESTRAIL_URL);
    }

    /**
     * Get TestRail username
     * @return TestRail username (email)
     */
    public String getTestRailUsername() {
        return getConfigValue(TESTRAIL_USERNAME_KEY, "");
    }

    /**
     * Get TestRail API key
     * @return TestRail API key
     */
    public String getTestRailApiKey() {
        return getConfigValue(TESTRAIL_API_KEY_KEY, "");
    }

    /**
     * Get TestRail project ID
     * @return TestRail project ID
     */
    public int getTestRailProjectId() {
        return Integer.parseInt(getConfigValue(TESTRAIL_PROJECT_ID_KEY, DEFAULT_TESTRAIL_PROJECT_ID));
    }

    /**
     * Validates that all critical configuration is available
     * @throws IllegalStateException if critical config is missing
     */
    public void validateConfiguration() {
        if (getApiBaseUrl() == null || getApiBaseUrl().isEmpty()) {
            throw new IllegalStateException("API Base URL is not configured");
        }
        logger.info("Configuration validation passed");
    }
}
