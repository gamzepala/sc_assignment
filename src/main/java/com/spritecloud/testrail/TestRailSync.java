package com.spritecloud.testrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utility to sync feature files with TestRail.
 * Creates test cases in TestRail from Cucumber feature files.
 *
 * Usage: Run this class manually to create test cases in TestRail
 */
public class TestRailSync {

    private static final Logger logger = LoggerFactory.getLogger(TestRailSync.class);
    private static final Pattern SCENARIO_PATTERN = Pattern.compile("^\\s*Scenario:\\s*(.+)$");
    private static final Pattern TAG_PATTERN = Pattern.compile("@(\\w+)");

    public static void main(String[] args) {
        logger.info("Starting TestRail sync...");

        try {
            TestRailConfig config = new TestRailConfig();
            config.validate();

            TestRailClient client = new TestRailClient(config);

            // Create API test suite
            logger.info("========================================");
            logger.info("Syncing API Test Suite");
            logger.info("========================================");
            int apiSuiteId = client.getOrCreateSuite(
                    "API Test Automation",
                    "Automated API tests covering authentication, products, users, and cart functionality"
            );

            // Sync API feature files
            syncFeatureFiles("src/test/resources/features/api", apiSuiteId, client, "API");

            // Create UI test suite
            logger.info("========================================");
            logger.info("Syncing UI Test Suite");
            logger.info("========================================");
            int uiSuiteId = client.getOrCreateSuite(
                    "UI Test Automation",
                    "Automated UI tests covering login, checkout, and sorting functionality using Playwright"
            );

            // Sync UI feature files
            syncFeatureFiles("src/test/resources/features/ui", uiSuiteId, client, "UI");

            logger.info("========================================");
            logger.info("TestRail sync completed successfully!");
            logger.info("========================================");
            logger.info("Next steps:");
            logger.info("1. Check TestRail to see all created test cases");
            logger.info("2. Add @C<id> tags to your feature file scenarios");
            logger.info("3. Run tests with TESTRAIL_ENABLED=true to report results");

        } catch (Exception e) {
            logger.error("TestRail sync failed", e);
        }
    }

    private static void syncFeatureFiles(String featurePath, int suiteId, TestRailClient client, String automationType) {
        try (Stream<Path> paths = Files.walk(Paths.get(featurePath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".feature"))
                    .forEach(path -> processFeatureFile(path, suiteId, client, automationType));
        } catch (IOException e) {
            logger.error("Failed to process feature files", e);
        }
    }

    private static void processFeatureFile(Path featureFile, int suiteId, TestRailClient client, String automationType) {
        logger.info("Processing feature file: {}", featureFile.getFileName());

        try {
            List<String> lines = Files.readAllLines(featureFile);
            List<String> currentTags = new ArrayList<>();

            for (String line : lines) {
                // Collect tags
                if (line.trim().startsWith("@")) {
                    Matcher tagMatcher = TAG_PATTERN.matcher(line);
                    while (tagMatcher.find()) {
                        currentTags.add("@" + tagMatcher.group(1));
                    }
                    continue;
                }

                // Process scenario
                Matcher scenarioMatcher = SCENARIO_PATTERN.matcher(line);
                if (scenarioMatcher.find()) {
                    String scenarioTitle = scenarioMatcher.group(1).trim();

                    // Skip if already has TestRail ID
                    if (currentTags.stream().anyMatch(tag -> tag.matches("@C\\d+"))) {
                        logger.info("  Skipping (already has TestRail ID): {}", scenarioTitle);
                        currentTags.clear();
                        continue;
                    }

                    // Create test case in TestRail
                    int caseId = client.createTestCase(suiteId, scenarioTitle, automationType, currentTags);

                    if (caseId > 0) {
                        logger.info("  ✓ Created case C{}: {}", caseId, scenarioTitle);
                        logger.info("    Add this tag to the scenario: @C{}", caseId);
                    }

                    currentTags.clear();
                }
            }

        } catch (IOException e) {
            logger.error("Failed to process feature file: {}", featureFile, e);
        }
    }
}
