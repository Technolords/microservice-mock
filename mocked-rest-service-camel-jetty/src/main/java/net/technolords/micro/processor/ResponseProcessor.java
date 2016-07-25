package net.technolords.micro.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;

/**
 * Created by Technolords on 2016-Jul-20.
 */
public class ResponseProcessor implements Processor {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
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
        String message = exchange.getIn().getBody(String.class);
        String response;
        if (ConfigurationManager.HTTP_POST.equals(requestType.toUpperCase())) {
            response = this.configurationManager.findResponseForPostOperationWithPathAndMessage(requestURI, message);
        } else {
            response = this.configurationManager.findResponseForGetOperationWithPath(requestURI);
        }
        if (response != null) {
            exchange.getIn().setBody(response);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            exchange.getIn().setHeader("Content-Type", "application/json");
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
        }
    }

}
