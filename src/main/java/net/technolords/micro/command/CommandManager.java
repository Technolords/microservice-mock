package net.technolords.micro.command;

import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.model.ResponseContext;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final String CONFIG = "config";
    private static final String LOG = "log";
    private static final String RESET = "reset";
    private static final String STATS = "stats";
    private static final String STOP = "stop";

    /**
     * Auxiliary method that executes a command by delegation (provided the command is supported).
     *
     * @param exchange
     *  The exchange associated with the command.
     *
     * @return
     *  The result of the command execution.
     */
    public static ResponseContext executeCommand(Exchange exchange) {
        Map<String, Object> commands = exchange.getIn().getHeaders();
        for (String key : commands.keySet()) {
            LOGGER.debug("Key: {} -> value: {}", key, commands.get(key));
        }
        if (commands.containsKey(CONFIG)) {
            return ConfigCommand.executeCommand();
        }
        if (commands.containsKey(LOG)) {
            return LogCommand.executeCommand((String) commands.get(LOG));
        }
        if (commands.containsKey(RESET)) {
            return ResetCommand.executeCommand();
        }
        if (commands.containsKey(STATS)) {
            return StatsCommand.executeCommand((String) commands.get(STATS));
        }
        if (commands.containsKey(STOP)) {
            return StopCommand.executeCommand(exchange);
        }
        return createUnsupportedResponse();
    }

    /**
     * Auxiliary method that generates the result of an unsupported command.
     *
     * @return
     *  The result of an unsupported command.
     */
    private static ResponseContext createUnsupportedResponse() {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_NOT_IMPLEMENTED));
        responseContext.setResponse("Currently not supported");
        return responseContext;
    }

}
