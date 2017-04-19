package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.apache.camel.Exchange;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.registry.MockRegistry;

public class ResetCommand implements Command {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Auxiliary method to get the id associated with this command.
     *
     * @return
     *  The id associated with the command.
     */
    @Override
    public String getId() {
        return Command.RESET;
    }

    /**
     * Auxiliary method that resets the statistics. Note that the StatisticsHandler is fetched from the Registry, which
     * is in fact a Jetty component.
     *
     * @return
     *  The result of the reset command.
     */
    @Override
    public ResponseContext executeCommand(Exchange exchange) {
        LOGGER.debug("Reset command called...");
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        StatisticsHandler statisticsHandler = MockRegistry.findStatisticsHandler();
        if (statisticsHandler != null) {
            statisticsHandler.statsReset();
            responseContext.setResponse("Statistics has been reset");
        } else {
            responseContext.setResponse("Unable to retrieve statistics (no handler configured)");
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_NOT_FOUND));
        }
        return responseContext;
    }

}
