package com.spritecloud.api.hooks;

import com.spritecloud.api.mock.MockApiServer;
import com.spritecloud.api.steps.TestContext;
import com.spritecloud.config.ConfigurationManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber hooks for API test lifecycle management.
 * Handles setup, teardown, and logging for API test scenarios.
 */
public class ApiHooks {

    private static final Logger logger = LoggerFactory.getLogger(ApiHooks.class);
    private final TestContext context;
    private final ConfigurationManager config;

    /**
     * Starts the mock API server before all API tests (once per test run).
     * Only starts if MOCK_API environment variable is set to true.
     * This enables tests to run in CI/CD environments where Cloudflare blocks requests.
     */
    @BeforeAll
    public static void startMockServer() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        if (config.isMockApiEnabled()) {
            logger.info("========================================");
            logger.info("Mock API mode is ENABLED - starting WireMock server");
            logger.info("This bypasses Cloudflare protection in CI/CD");
            MockApiServer.start();
            logger.info("Mock server started at: {}", config.getMockApiUrl());
            logger.info("========================================");
        } else {
            logger.info("Mock API mode is DISABLED - using real API at: {}", config.getApiBaseUrl());
        }
    }

    /**
     * Stops the mock API server after all API tests complete.
     */
    @AfterAll
    public static void stopMockServer() {
        if (MockApiServer.isRunning()) {
            logger.info("Stopping mock API server");
            MockApiServer.stop();
            logger.info("Mock API server stopped successfully");
        }
    }

    /**
     * Constructs ApiHooks with shared test context.
     *
     * @param context shared context for API test data
     */
    public ApiHooks(TestContext context) {
        this.context = context;
        this.config = ConfigurationManager.getInstance();
    }

    /**
     * Executes before each scenario to initialize test environment.
     * Logs scenario details, validates configuration, and resets context.
     *
     * @param scenario the Cucumber scenario being executed
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("========================================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("Tags: {}", scenario.getSourceTagNames());
        logger.info("Environment: {}", config.getEnvironment());
        logger.info("API Base URL: {}", config.getApiBaseUrl());
        logger.info("========================================");

        config.validateConfiguration();
        context.reset();
    }

    /**
     * Executes after each scenario to log results and perform cleanup.
     * Captures detailed error information for failed scenarios.
     *
     * @param scenario the Cucumber scenario that was executed
     */
    @After
    public void afterScenario(Scenario scenario) {
        logger.info("========================================");
        logger.info("Finished Scenario: {}", scenario.getName());
        logger.info("Status: {}", scenario.getStatus());

        if (scenario.isFailed()) {
            logger.error("Scenario FAILED: {}", scenario.getName());

            if (context.getResponse() != null) {
                logger.error("Response Status: {}", context.getResponse().getStatusCode());
                logger.error("Response Body: {}", context.getResponse().getBody().asPrettyString());
            }
        } else {
            logger.info("Scenario PASSED: {}", scenario.getName());
        }

        logger.info("========================================\n");
    }

    /**
     * Executes before API-specific scenarios.
     * Tagged with @API in feature files.
     *
     * @param scenario the Cucumber scenario being executed
     */
    @Before("@API")
    public void beforeApiScenario(Scenario scenario) {
        logger.info("Initializing API test scenario");
    }

    /**
     * Executes after negative test scenarios to log error responses.
     * Tagged with @NegativeTest in feature files.
     *
     * @param scenario the Cucumber scenario that was executed
     */
    @After("@NegativeTest")
    public void afterNegativeTest(Scenario scenario) {
        logger.info("Completed negative test scenario");

        if (context.getResponse() != null) {
            logger.info("Expected error response received with status: {}",
                    context.getResponse().getStatusCode());
        }
    }
}
