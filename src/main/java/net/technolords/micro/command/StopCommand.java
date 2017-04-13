package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.model.ResponseContext;

public class StopCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);

    /**
     * Auxiliary method to get the id associated with this command.
     *
     * @return
     *  The id associated with the command.
     */
    @Override
    public String getId() {
        return Command.STOP;
    }

    /**
     * Auxiliary method that stops the Main execution. This is achieved by calling stop on the CamelContext
     * which is associated with the Main.
     *
     * @param exchange
     *  The exchange associated with the stop command.
     *
     * @return
     *  The result of the stop command (unlikely to be received, as the execution is terminating).
     */
    @Override
    public ResponseContext executeCommand(Exchange exchange) {
        LOGGER.debug("Stop command called...");
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        try {
            exchange.getContext().stop();
            responseContext.setResponse("Stopping the mock..");
        } catch (Exception e) {
            responseContext.setResponse(e.getMessage());
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR));
        }
        return responseContext;
    }
}
