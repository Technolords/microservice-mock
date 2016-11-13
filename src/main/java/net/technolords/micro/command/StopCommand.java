package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.domain.ResponseContext;

public class StopCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);

    public static ResponseContext executeCommand(Exchange exchange) {
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
