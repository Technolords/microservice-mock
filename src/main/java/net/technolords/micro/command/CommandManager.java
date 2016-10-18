package net.technolords.micro.command;

import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.ResponseContext;

public class CommandManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String LOG = "log";
    private static final String STOP = "stop";
    private static final String STATS = "stats";
    private static final String RESET = "reset";

    public ResponseContext executeCommand(Exchange exchange) {
        Map<String, Object> commands = exchange.getIn().getHeaders();
        for (String key : commands.keySet()) {
            LOGGER.debug("Key: {} -> value: {}", key, commands.get(key));
        }
        if (commands.containsKey(LOG)) {
            return LogCommand.executeCommand((String) commands.get(LOG));
        }
        if (commands.containsKey(STOP)) {
            return StopCommand.executeCommand(exchange);
        }
        if (commands.containsKey(STATS)) {
            return StatsCommand.executeCommand((String) commands.get(STATS));
        }
        if (commands.containsKey(RESET)) {
            return ResetCommand.executeCommand();
        }
        return this.createUnsupportedResponse();
    }

    private ResponseContext createUnsupportedResponse() {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_NOT_IMPLEMENTED));
        responseContext.setResponse("Currently not supported");
        return responseContext;
    }

}
