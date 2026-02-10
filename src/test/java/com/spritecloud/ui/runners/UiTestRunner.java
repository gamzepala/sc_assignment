package com.spritecloud.ui.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform Suite runner for all UI tests.
 * Runs all Cucumber scenarios from the features/ui directory using Playwright.
 *
 * <p>Test Configuration:
 * <ul>
 *   <li>Features: features/ui/**</li>
 *   <li>Browser: Chromium (headless)</li>
 *   <li>Reports: HTML, JSON, and JUnit XML formats</li>
 *   <li>Glue: com.spritecloud.ui</li>
 * </ul>
 *
 * <p>Usage: Run this class to execute all UI tests
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/ui")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.spritecloud.ui")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, " +
                "html:target/cucumber-reports/ui/cucumber.html, " +
                "json:target/cucumber-reports/ui/cucumber.json, " +
                "junit:target/cucumber-reports/ui/cucumber.xml"
)
public class UiTestRunner {
}
