package net.technolords.micro.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.ResponseContext;
import net.technolords.micro.config.ConfigurationManager;

public class ResponseProcessor implements Processor {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String CONTENT_TYPE = "Content-Type";
    private ConfigurationManager configurationManager = null;

    public ResponseProcessor(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    /**
     * Processor that calls the configuration manager to find a response, based URI as well as request type
     * GET or POST (and a message when it was a POST).
     *
     * @param exchange
     *  The exchange associated with the request.
     * @throws Exception
     *  When the configuration manager has an error.
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        LOGGER.debug("About to generate response...");
        String requestType = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        ResponseContext responseContext;
        if (ConfigurationManager.HTTP_POST.equals(requestType.toUpperCase())) {
            String message = exchange.getIn().getBody(String.class);
            LOGGER.debug("Current body: {}", message);
            responseContext = this.configurationManager.findResponseForPostOperationWithPathAndMessage(requestURI, message);
        } else {
            responseContext = this.configurationManager.findResponseForGetOperationWithPath(requestURI);
        }
        if (responseContext != null) {
            exchange.getIn().setBody(responseContext.getResponse());
            exchange.getIn().setHeader(CONTENT_TYPE, responseContext.getContentType());
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, (responseContext.getErrorCode() == null ? 200 : responseContext.getErrorCode()));
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
        }
    }

}
