package net.technolords.micro.log;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.technolords.micro.model.ResponseContext;

public class LogManagerTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATASET_FOR_CHANGING_LOG_LEVELS = "datasetForChangingLogLevels";
    private static final String GROUP_CHANGE_CONFIG = "changeConfig";
    private static final String GROUP_CHANGE_LEVEL = "changeLevel";

    /**
     * This test asserts that after re-configuration of the log engine the total appenders are
     * different. The 'comparison' will be done between:
     *
     * - default config (src/main/resources/log4j2.xml)
     * - alternative config (src/test/resources/xml/config-for-LogManagerTest.xml)
     */
    @Test (groups = { GROUP_CHANGE_CONFIG }, description = "Validate the numbers of Appenders associated with the LoggerContext")
    public void testLogReconfiguration() {
        LOGGER.debug("About to test re-configuration of the logger context");
        final String pathToAlternativeLogConfig = "src/test/resources/config/log/config-for-LogManagerTest.xml";
        LoggerContext loggerContext = LoggerContext.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        Map<String, Appender> appenderMap = configuration.getAppenders();
        Assert.assertTrue(appenderMap.size() == 1, "Expected 1 appender");
        Assert.assertTrue(appenderMap.containsKey("console"));

        LogManager.initializeLogging(pathToAlternativeLogConfig);
        // Refresh reference of configuration as it changed
        configuration = loggerContext.getConfiguration();
        appenderMap = configuration.getAppenders();
        Assert.assertTrue(appenderMap.size() == 2, "Expected 2 appenders");
        Assert.assertTrue(appenderMap.containsKey("console"));
        Assert.assertTrue(appenderMap.containsKey("filterAppender"));
    }

    /**
     * Auxiliary method to declare a data set to support changing log levels. An entry is specified
     * with three elements, each meaning:
     *
     *  [0] : The new log level to set
     *  [1] : The expected log level
     *  [2] : The expected message
     *
     * @return
     *  The data set.
     */
    @DataProvider(name = DATASET_FOR_CHANGING_LOG_LEVELS)
    public Object[][] dataSetMock(){
        return new Object[][] {
            { "error", Level.ERROR, "Log level changed to ERROR" },
            { "warn", Level.WARN, "Log level changed to WARN" },
            { "info", Level.INFO, "Log level changed to INFO" },
            { "debug", Level.DEBUG, "Log level changed to DEBUG" },
            { "off", Level.OFF, "Logging switched off" },
            { "oops", Level.INFO, "Log level changed to INFO" },
        };
    }

    /**
     * Auxiliary method to reset the log info, in case logging is actually required during development
     * of the test.
     */
    @BeforeMethod (groups = { GROUP_CHANGE_LEVEL })
    public void resetLogLevelToInfo() {
        LoggerConfig rootLogger = this.getRootLogger();
        rootLogger.setLevel(Level.INFO);
    }

    /**
     * This test asserts the new log level has been set as well as the expected message
     * to be returned is correct.
     *
     * @param newLevel
     *  The new log level.
     * @param expectedLevel
     *  The expected log level.
     * @param expectedMessage
     *  The expected message.
     */
    @Test (dataProvider = DATASET_FOR_CHANGING_LOG_LEVELS, groups = { GROUP_CHANGE_LEVEL} )
    public void testChangeLogLevels(final String newLevel, final Level expectedLevel, final String expectedMessage) {
        ResponseContext responseContext = LogManager.changeLogLevel(newLevel);
        LoggerConfig rootLogger = this.getRootLogger();
        Assert.assertTrue(rootLogger.getLevel().equals(expectedLevel));
        Assert.assertEquals(responseContext.getResponse(), expectedMessage);
    }

    /**
     * Auxiliary method to find the root logger.
     *
     * @return
     *  A reference of the root logger.
     */
    private LoggerConfig getRootLogger() {
        LoggerContext loggerContext = LoggerContext.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        return configuration.getRootLogger();
    }

}