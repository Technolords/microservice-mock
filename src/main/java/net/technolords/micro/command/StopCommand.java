package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.apache.camel.Exchange;

import net.technolords.micro.ResponseContext;

public class StopCommand {

    public static ResponseContext executeCommand(Exchange exchange) {
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
