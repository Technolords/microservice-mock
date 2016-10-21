package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.StandardLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.processor.ResponseContext;

public class LogCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCommand.class);

    public static ResponseContext executeCommand(String logLevel) {
        LOGGER.debug("Log command called, with log level {}", logLevel);
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
