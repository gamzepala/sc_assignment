package com.spritecloud.ui.testrail;

import com.spritecloud.testrail.TestRailClient;
import com.spritecloud.testrail.TestRailConfig;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cucumber hook for reporting UI test results to TestRail.
 * Automatically creates test runs and reports pass/fail status for UI tests.
 */
public class UiTestRailReporter {

    private static final Logger logger = LoggerFactory.getLogger(UiTestRailReporter.class);
    private static TestRailClient client;
    private static TestRailConfig config;
    private static Map<String, Integer> scenarioToCaseIdMap = new HashMap<>();
    private static final Pattern CASE_ID_PATTERN = Pattern.compile("@C(\\d+)");

    /**
     * Initialize TestRail client and create test run before all tests
     */
    @BeforeAll
    public static void initializeTestRail() {
        try {
            config = new TestRailConfig();

            if (!config.isEnabled()) {
                logger.info("TestRail integration is disabled - skipping initialization");
                return;
            }

            client = new TestRailClient(config);

            // Create or get UI test suite
            int suiteId = client.getOrCreateSuite(
                    "UI Test Automation",
                    "Automated UI tests using Playwright for SauceDemo"
            );

            // Create a new test run for this execution
            String runName = "UI Test Run - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String description = "Automated UI test execution from CI/CD pipeline";

            client.createTestRun(runName, description, null);

            logger.info("TestRail reporter initialized successfully for UI tests");

        } catch (Exception e) {
            logger.error("Failed to initialize TestRail reporter for UI tests", e);
            // Don't fail the tests if TestRail fails
            client = null;
        }
    }

    /**
     * Extract TestRail case ID from scenario tags
     */
    @Before
    public void extractCaseId(Scenario scenario) {
        if (client == null || !config.isEnabled()) {
            return;
        }

        // Look for @C123 tag format
        for (String tag : scenario.getSourceTagNames()) {
            Matcher matcher = CASE_ID_PATTERN.matcher(tag);
            if (matcher.find()) {
                int caseId = Integer.parseInt(matcher.group(1));
                scenarioToCaseIdMap.put(scenario.getName(), caseId);
                logger.debug("Mapped UI scenario '{}' to TestRail case C{}", scenario.getName(), caseId);
                break;
            }
        }
    }

    /**
     * Report test result to TestRail after each scenario
     */
    @After
    public void reportResult(Scenario scenario) {
        if (client == null || !config.isEnabled()) {
            return;
        }

        Integer caseId = scenarioToCaseIdMap.get(scenario.getName());
        if (caseId == null) {
            logger.debug("No TestRail case ID found for UI scenario: {}", scenario.getName());
            return;
        }

        try {
            int statusId;
            String comment;

            if (scenario.isFailed()) {
                statusId = TestRailClient.STATUS_FAILED;
                comment = "UI test failed: " + scenario.getName();
            } else {
                statusId = TestRailClient.STATUS_PASSED;
                comment = "UI test passed successfully";
            }

            // Calculate elapsed time in seconds (approximate)
            long elapsedSeconds = 5; // UI tests typically take longer

            client.addTestResult(caseId, statusId, comment, elapsedSeconds);

        } catch (Exception e) {
            logger.error("Failed to report result for UI scenario: {}", scenario.getName(), e);
        }
    }

    /**
     * Close test run after all tests complete
     */
    @AfterAll
    public static void closeTestRail() {
        if (client != null && config.isEnabled()) {
            try {
                client.closeTestRun();
                logger.info("TestRail UI test run closed successfully");
            } catch (Exception e) {
                logger.error("Failed to close TestRail UI test run", e);
            }
        }
    }
}
