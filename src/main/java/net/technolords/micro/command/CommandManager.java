package net.technolords.micro.command;

import java.net.HttpURLConnection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import net.technolords.micro.ResponseContext;

public class CommandManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String LOG = "log";
    private static final String STOP = "stop";
    private static final String STATS = "stats";
    private static final String RESET = "reset";

    public ResponseContext executeCommand(Map<String, Object> commands) {
        LOGGER.info("About to discover command...");
        for (String key : commands.keySet()) {
            LOGGER.info("Key: {} -> value: {}", key, commands.get(key));
        }
        if (commands.containsKey(LOG)) {
            return this.handleLogCommand();
        }
        if (commands.containsKey(STOP)) {
            return this.handleStopCommand();
        }
        return this.createUnsupportedResponse();
    }

    private ResponseContext handleLogCommand() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        if (rootLogger != null) {
            rootLogger.setLevel(Level.DEBUG);
        }
        return this.createSuccessfulResponse();
    }

    private ResponseContext handleStopCommand() {
        // TODO: exchange.getContext().stop(); ??
        // dump stats?
        return this.createSuccessfulResponse();
    }

    private ResponseContext createSuccessfulResponse() {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        responseContext.setResponse("Success");
        return responseContext;
    }

    private ResponseContext createUnsupportedResponse() {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_NOT_IMPLEMENTED));
        responseContext.setResponse("Currently not supported");
        return responseContext;
    }
}
