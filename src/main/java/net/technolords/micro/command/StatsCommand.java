package net.technolords.micro.command;

import java.net.HttpURLConnection;

import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.registry.MockRegistry;

public class StatsCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommand.class);
    private static StatisticsHandler statisticsHandler;

    /**
     * Auxiliary method that reports the statistics. Note that the StatisticsHandler is fetched from the Registry,
     * which is in fact a Jetty component.
     *
     * @param type
     *  The type associated with the report/stats. When the type is 'html' the report is generated in HTML format,
     *  otherwise a custom String is returned.
     *
     * @return
     *  The result of the stats command.
     */
    public static ResponseContext executeCommand(String type) {
        LOGGER.debug("Stats command called...");
        ResponseContext responseContext = new ResponseContext();
        if (statisticsHandler == null) {
            statisticsHandler = MockRegistry.findStatisticsHandler();
        }
        if (statisticsHandler != null) {
            switch (type.toUpperCase()) {
                case "HTML":
                    responseContext.setResponse(statisticsHandler.toStatsHTML());
                    responseContext.setContentType(ResponseContext.HTML_CONTENT_TYPE);
                    break;
                default:
                    responseContext.setResponse(statisticsAsString(statisticsHandler));
                    responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
                    break;
            }
        } else {
            responseContext.setResponse("Unable to retrieve statistics (no handler configured)");
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_NOT_FOUND));
        }
        return responseContext;
    }

    private static String statisticsAsString(StatisticsHandler statisticsHandler) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Total requests: ").append(statisticsHandler.getDispatched());
        buffer.append(", total request time: ").append(statisticsHandler.getRequestTimeTotal());
        buffer.append(", mean time: ").append(statisticsHandler.getRequestTimeMean());
        buffer.append(", max time: ").append(statisticsHandler.getRequestTimeMax());
        buffer.append(", std dev: ").append(statisticsHandler.getRequestTimeStdDev());
        buffer.append(", last reset/start: ").append(statisticsHandler.getStatsOnMs()).append(" ms ago");
        return buffer.toString();
    }
}
