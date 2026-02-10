package com.spritecloud.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Runs critical test scenarios tagged with @Smoke for quick validation.
 *
 * <p>Purpose: Smoke tests verify core functionality and act as a quick health check
 * before running the full test suite. 
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, " +
                "html:target/cucumber-reports/smoke/cucumber.html, " +
                "json:target/cucumber-reports/smoke/cucumber.json, " +
                "junit:target/cucumber-reports/smoke/cucumber.xml")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.spritecloud.api.steps,com.spritecloud.api.hooks," +
                "com.spritecloud.ui.steps,com.spritecloud.ui.hooks")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME,
        value = "@Smoke")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME,
        value = "true")
@ConfigurationParameter(key = EXECUTION_DRY_RUN_PROPERTY_NAME,
        value = "false")
public class SmokeTestRunner {
}
