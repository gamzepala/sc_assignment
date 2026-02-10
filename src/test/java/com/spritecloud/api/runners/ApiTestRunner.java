package com.spritecloud.api.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Suite runner for all API tests.
 * Runs all Cucumber scenarios tagged with @API from the features/api directory.
 *
 * <p>Test Configuration:
 * <ul>
 *   <li>Features: features/api/**</li>
 *   <li>Tags: @API and not @Ignore</li>
 *   <li>Reports: HTML, JSON, and JUnit XML formats</li>
 *   <li>Glue: com.spritecloud.api.steps, com.spritecloud.api.hooks</li>
 * </ul>
 *
 * <p>Usage: Run this class to execute all API tests
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, " +
                "html:target/cucumber-reports/api/cucumber.html, " +
                "json:target/cucumber-reports/api/cucumber.json, " +
                "junit:target/cucumber-reports/api/cucumber.xml")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.spritecloud.api.steps,com.spritecloud.api.hooks")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME,
        value = "@API and not @Ignore")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME,
        value = "true")
@ConfigurationParameter(key = EXECUTION_DRY_RUN_PROPERTY_NAME,
        value = "false")
public class ApiTestRunner {
}
