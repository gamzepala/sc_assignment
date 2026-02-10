package com.spritecloud.ui.hooks;

import com.spritecloud.config.ConfigurationManager;
import com.spritecloud.ui.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber hooks for UI test lifecycle management with Playwright.
 * Handles browser initialization, screenshot capture, and cleanup.
 *
 * <p>Design Decision: Manages browser lifecycle at both suite and scenario levels.
 * Automatically captures screenshots for failed scenarios to aid debugging.
 */
public class UiHooks {

    private static final Logger logger = LoggerFactory.getLogger(UiHooks.class);
    private final TestContext context;

    /**
     * Constructs UiHooks with shared test context.
     *
     * @param context shared context for UI test data and browser management
     */
    public UiHooks(TestContext context) {
        this.context = context;
    }

    /**
     * Executes once before all scenarios in the suite.
     * Initializes configuration for UI tests.
     */
    @BeforeAll
    public static void setupSuite() {
        logger.info("========================================");
        logger.info("Starting UI Test Suite");
        logger.info("========================================");
        ConfigurationManager.getInstance();
    }

    /**
     * Executes before each scenario to set up browser and page context.
     * Initializes Playwright, launches browser, and creates new context.
     *
     * @param scenario the Cucumber scenario being executed
     */
    @Before
    public void setupScenario(Scenario scenario) {
        logger.info("========================================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("Tags: {}", scenario.getSourceTagNames());
        logger.info("========================================");

        context.initializeBrowser();
        context.createNewContext();

        logger.info("Browser initialized for scenario: {}", scenario.getName());
    }

    /**
     * Executes after each scenario to clean up and capture failure evidence.
     * Automatically takes screenshots for failed scenarios and logs page URL.
     *
     * @param scenario the Cucumber scenario that was executed
     */
    @After
    public void teardownScenario(Scenario scenario) {
        logger.info("========================================");
        logger.info("Finished Scenario: {}", scenario.getName());
        logger.info("Status: {}", scenario.getStatus());

        if (scenario.isFailed()) {
            logger.error("Scenario FAILED: {}", scenario.getName());

            try {
                byte[] screenshot = context.getPage().screenshot();
                scenario.attach(screenshot, "image/png", "failure-screenshot");
                logger.info("Screenshot captured for failed scenario");
            } catch (Exception e) {
                logger.error("Failed to capture screenshot: {}", e.getMessage());
            }

            String currentUrl = context.getPage().url();
            logger.error("Page URL at failure: {}", currentUrl);
        } else {
            logger.info("Scenario PASSED: {}", scenario.getName());
        }

        logger.info("========================================");

        context.closeContext();
        context.clearExpectedCartItems();
    }

    /**
     * Executes once after all scenarios in the suite.
     * Final cleanup and logging for the test run.
     */
    @AfterAll
    public static void teardownSuite() {
        logger.info("========================================");
        logger.info("Finished UI Test Suite");
        logger.info("========================================");
    }
}
