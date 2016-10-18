package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import net.technolords.micro.ResponseContext;

public class LogCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCommand.class);

    public static ResponseContext executeCommand(String logLevel) {
        LOGGER.debug("Got log level {}", logLevel);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        if (rootLogger != null) {
            switch (Level.toLevel(logLevel, Level.INFO).toInt()) {
                case Level.ERROR_INT:
                    rootLogger.setLevel(Level.ERROR);
                    responseContext.setResponse("Log level changed to ERROR");
                    break;
                case Level.WARN_INT:
                    rootLogger.setLevel(Level.WARN);
                    responseContext.setResponse("Log level changed to WARN");
                    break;
                case Level.DEBUG_INT:
                    rootLogger.setLevel(Level.DEBUG);
                    responseContext.setResponse("Log level changed to DEBUG");
                    break;
                case Level.INFO_INT:
                    rootLogger.setLevel(Level.INFO);
                    responseContext.setResponse("Log level changed to INFO");
                    break;
                default:
                    responseContext.setResponse("Log level unchanged, unsupported level: " + logLevel);
            }
        } else {
            responseContext.setResponse("Unable to change log level, no ROOT logger found...");
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR));
        }
        return responseContext;
    }

}
