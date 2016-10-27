package net.technolords.micro.log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.impl.DefaultConfigurationBuilder;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.spi.StandardLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.processor.ResponseContext;

public class LogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogManager.class);

    public static void initializeLogging(String pathToLogConfiguration) {
        LOGGER.info("Path to log configuration: {}", pathToLogConfiguration);
        Path path = FileSystems.getDefault().getPath(pathToLogConfiguration);
        try {
            LOGGER.info("Log configuration file exist: {}", Files.exists(path));
            LoggerContext loggerContext = LoggerContext.getContext(false);
            LOGGER.info("Logger context: {} -> state: {}", loggerContext, loggerContext.getState());
            ConfigurationSource configurationSource = new ConfigurationSource(Files.newInputStream(path, StandardOpenOption.READ));
            XmlConfiguration xmlConfiguration = new XmlConfiguration(loggerContext, configurationSource);
            xmlConfiguration.initialize();
            loggerContext.updateLoggers();
            LOGGER.info("Logger context initialized: {} -> state: {}", loggerContext, loggerContext.getState());
        } catch (IOException e) {
            LOGGER.warn("Unable to read log configuration -> ignoring config and using default", e);
        }
    }

    public static ResponseContext changeLogLevel(String logLevel) {
        LoggerContext loggerContext = LoggerContext.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig rootLogger = configuration.getRootLogger();
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
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
