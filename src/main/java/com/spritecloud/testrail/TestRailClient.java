package com.spritecloud.testrail;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Client for interacting with TestRail API.
 * Handles test case creation, test run management, and result reporting.
 */
public class TestRailClient {

    private static final Logger logger = LoggerFactory.getLogger(TestRailClient.class);
    private final TestRail testRail;
    private final TestRailConfig config;
    private Integer suiteId;
    private Integer runId;

    // TestRail status IDs
    public static final int STATUS_PASSED = 1;
    public static final int STATUS_BLOCKED = 2;
    public static final int STATUS_UNTESTED = 3;
    public static final int STATUS_RETEST = 4;
    public static final int STATUS_FAILED = 5;

    public TestRailClient(TestRailConfig config) {
        this.config = config;
        config.validate();

        this.testRail = TestRail.builder(config.getUrl(), config.getUsername(), config.getApiKey())
                .applicationName("SpriteCloud Test Automation Framework")
                .build();

        logger.info("TestRail client initialized successfully");
    }

    /**
     * Gets or creates a test suite for API tests
     */
    public int getOrCreateSuite(String suiteName, String description) {
        try {
            // Try to find existing suite
            List<Suite> suites = testRail.suites().list(config.getProjectId()).execute();
            for (Suite suite : suites) {
                if (suite.getName().equals(suiteName)) {
                    logger.info("Found existing suite: {} (ID: {})", suiteName, suite.getId());
                    this.suiteId = suite.getId();
                    return suite.getId();
                }
            }

            // Create new suite if not found
            Suite newSuite = testRail.suites().add(config.getProjectId(),
                    new Suite().setName(suiteName).setDescription(description)).execute();
            logger.info("Created new suite: {} (ID: {})", suiteName, newSuite.getId());
            this.suiteId = newSuite.getId();
            return newSuite.getId();

        } catch (Exception e) {
            logger.error("Failed to get or create suite: {}", suiteName, e);
            throw new RuntimeException("TestRail suite creation failed", e);
        }
    }

    /**
     * Creates a test case in TestRail
     */
    public int createTestCase(int suiteId, String title, String automationType, List<String> tags) {
        try {
            Case testCase = new Case()
                    .setTitle(title)
                    .setTypeId(1) // Automated
                    .setPriorityId(tags.contains("@Smoke") ? 4 : 2); // Critical for smoke, Medium for others

            Case createdCase = testRail.cases().add(suiteId, testCase, new ArrayList<>()).execute();
            logger.info("Created test case: {} (ID: C{})", title, createdCase.getId());
            return createdCase.getId();

        } catch (Exception e) {
            logger.error("Failed to create test case: {}", title, e);
            return -1; // Return -1 if creation fails (non-blocking)
        }
    }

    /**
     * Creates a test run for the current test execution
     */
    public int createTestRun(String runName, String description, List<Integer> caseIds) {
        try {
            Run run = new Run()
                    .setName(runName)
                    .setDescription(description)
                    .setSuiteId(suiteId)
                    .setIncludeAll(caseIds == null || caseIds.isEmpty());

            if (caseIds != null && !caseIds.isEmpty()) {
                run.setCaseIds(caseIds);
            }

            Run createdRun = testRail.runs().add(config.getProjectId(), run).execute();
            this.runId = createdRun.getId();
            logger.info("Created test run: {} (ID: {})", runName, createdRun.getId());
            return createdRun.getId();

        } catch (Exception e) {
            logger.error("Failed to create test run: {}", runName, e);
            throw new RuntimeException("TestRail run creation failed", e);
        }
    }

    /**
     * Adds a test result to TestRail
     */
    public void addTestResult(int caseId, int statusId, String comment, long elapsedSeconds) {
        if (runId == null) {
            logger.warn("No active test run - skipping result for case C{}", caseId);
            return;
        }

        try {
            Result result = new Result()
                    .setStatusId(statusId)
                    .setComment(comment);

            if (elapsedSeconds > 0) {
                result.setElapsed(elapsedSeconds + "s");
            }

            testRail.results().addForCase(runId, caseId, result, new ArrayList<>()).execute();
            logger.info("Added result for case C{}: {} - {}", caseId, getStatusName(statusId), comment);

        } catch (Exception e) {
            logger.error("Failed to add result for case C{}", caseId, e);
        }
    }

    /**
     * Closes the current test run
     */
    public void closeTestRun() {
        if (runId == null) {
            logger.warn("No active test run to close");
            return;
        }

        try {
            testRail.runs().close(runId).execute();
            logger.info("Closed test run: {}", runId);
            runId = null;
        } catch (Exception e) {
            logger.error("Failed to close test run: {}", runId, e);
        }
    }

    private String getStatusName(int statusId) {
        return switch (statusId) {
            case STATUS_PASSED -> "PASSED";
            case STATUS_FAILED -> "FAILED";
            case STATUS_BLOCKED -> "BLOCKED";
            case STATUS_RETEST -> "RETEST";
            default -> "UNTESTED";
        };
    }

    public Integer getSuiteId() {
        return suiteId;
    }

    public Integer getRunId() {
        return runId;
    }
}
