package net.technolords.micro.log;

import java.net.HttpURLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.StandardLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.processor.ResponseContext;

/**
 * This class has the responsibility to interface with the logging library, which is currently
 * log4j. All log4j dependencies and classes are used here solely, which limits the impact in
 * case things change.
 *
 * This class supports operations from two angles:
 * - startup time, where end user overrides default configuration (caller class: PropertiesManager)
 * - run time, where end-user changes log level (caller class: LogCommand)
 */
public class LogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogManager.class);

    /**
     * Auxiliary method to (re)initialize the logging. This method is typically called from
     * the PropertiesManager when a external log4j2 configuration is provided, while at the
     * same time no CLI parameter was given.
     *
     * Setting the new config location is enough, as it will trigger a reconfigure
     * automatically.
     *
     * @param pathToLogConfiguration
     *  A path to the external log configuration file.
     */
    public static void initializeLogging(String pathToLogConfiguration) {
        Path path = FileSystems.getDefault().getPath(pathToLogConfiguration);
        LOGGER.trace("Path to log configuration: {} -> file exists: {}", pathToLogConfiguration, Files.exists(path));
        LoggerContext loggerContext = LoggerContext.getContext(false);
        loggerContext.setConfigLocation(path.toUri());
    }

    /**
     * Auxiliary method to change the log level.
     *
     * @param logLevel
     *  The log level to set.
     *
     * @return
     *  A ResponseContext containing the result of the command.
     */
    public static ResponseContext changeLogLevel(String logLevel) {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        LoggerContext loggerContext = LoggerContext.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig rootLogger = configuration.getRootLogger();
        if (rootLogger != null) {
            switch (StandardLevel.getStandardLevel(Level.toLevel(logLevel, Level.INFO).intLevel())) {
                case ERROR:
                    rootLogger.setLevel(Level.ERROR);
                    responseContext.setResponse("Log level changed to ERROR");
                    break;
                case WARN:
                    rootLogger.setLevel(Level.WARN);
                    responseContext.setResponse("Log level changed to WARN");
                    break;
                case DEBUG:
                    rootLogger.setLevel(Level.DEBUG);
                    responseContext.setResponse("Log level changed to DEBUG");
                    break;
                case INFO:
                    rootLogger.setLevel(Level.INFO);
                    responseContext.setResponse("Log level changed to INFO");
                    break;
                case OFF:
                    rootLogger.setLevel(Level.OFF);
                    responseContext.setResponse("Logging switched off");
                    break;
                default:
                    responseContext.setResponse("Log level unchanged, unsupported level: " + logLevel);
            }
            loggerContext.updateLoggers();
        } else {
            responseContext.setResponse("Unable to change log level, no ROOT logger found...");
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR));
        }
        return responseContext;
    }
}
