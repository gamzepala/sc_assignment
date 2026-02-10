package com.spritecloud.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Runs test scenarios tagged with @Regression for full coverage.
 *
 * <p>Purpose: Regression tests provide thorough validation of all features
 * to ensure no functionality has broken. Run these before releases.
 *
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, " +
                "html:target/cucumber-reports/regression/cucumber.html, " +
                "json:target/cucumber-reports/regression/cucumber.json, " +
                "junit:target/cucumber-reports/regression/cucumber.xml")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.spritecloud.api.steps,com.spritecloud.api.hooks," +
                "com.spritecloud.ui.steps,com.spritecloud.ui.hooks")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME,
        value = "@Regression")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME,
        value = "true")
@ConfigurationParameter(key = EXECUTION_DRY_RUN_PROPERTY_NAME,
        value = "false")
public class RegressionTestRunner {
}
