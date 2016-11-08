package net.technolords.micro.processor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.command.CommandManager;
import net.technolords.micro.config.ConfigurationManager;

public class ResponseProcessor implements Processor {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String CONTENT_TYPE = "Content-Type";
    private ConfigurationManager configurationManager = null;
    private CommandManager commandManager = new CommandManager();

    public ResponseProcessor(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    /**
     * Processor that calls the configuration manager to find a response, based URI as well as request type
     * GET or POST (and a message when it was a POST). Unless, the GET request is a command for the mock
     * service itself, then it is delegated towards the CommandManager.
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
        ResponseContext responseContext;
        switch (requestType.toUpperCase()) {
            case ConfigurationManager.HTTP_GET:
                responseContext = this.handleGetRequest(exchange);
                break;
            case ConfigurationManager.HTTP_POST:
                responseContext = this.handlePostRequest(exchange);
                break;
            default:
                responseContext = this.handleUnsupportedRequest(exchange);
        }
        this.updateExchange(exchange, responseContext);
    }

    /**
     * Auxiliary method that delegates the request towards the configuration manager, based on the request URI.
     *
     * @param exchange
     *  The Camel exchange associated with the GET request.
     * @return
     *  A ResponseContext (or null when there was no match (this will trigger a 404)).
     *
     * @throws InterruptedException
     *  When the response with delay got interrupted.
     * @throws IOException
     *  When reading the response data failed.
     */
    private ResponseContext handleGetRequest(Exchange exchange) throws InterruptedException, IOException {
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        if (requestURI.equals("/mock/cmd")) {
            return this.commandManager.executeCommand(exchange);
        } else {
            return this.configurationManager.findResponseForGetOperationWithPath(requestURI);
        }
    }

    /**
     * Auxiliary method that delegates the request towards the configuration manager, based on the request URI as
     * well as the body associated with the POST request.
     *
     * @param exchange
     *  The Camel exchange associated with the POST request.
     * @return
     *  A ResponseContext (or null when there was no match (this will trigger a 404)).
     *
     * @throws InterruptedException
     *  When the response with delay got interrupted.
     * @throws XPathExpressionException
     *  When the Xpath failed, based on a XML body.
     * @throws IOException
     *  When reading the response data failed.
     */
    private ResponseContext handlePostRequest(Exchange exchange) throws InterruptedException, XPathExpressionException, IOException {
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String message = exchange.getIn().getBody(String.class);
        return this.configurationManager.findResponseForPostOperationWithPathAndMessage(requestURI, message);
    }

    /**
     * Auxiliary method that creates a ResponseContext stating the request type is not supported.
     *
     * @param exchange
     *  The Camel exchange associated with the unsupported request type.
     * @return
     *  A ResponseContext.
     */
    private ResponseContext handleUnsupportedRequest(Exchange exchange) {
        String requestType = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_NOT_IMPLEMENTED));
        responseContext.setResponse("Request type " + requestType + " not supported");
        return responseContext;
    }

    /**
     * Auxiliary method to update the Camel exchange, and basically sets the body (when applicable) and
     * the http response code.
     *
     * @param exchange
     *  The Camel exchange to update.
     * @param responseContext
     *  The ResponseContext associated with the update.
     */
    private void updateExchange(Exchange exchange, ResponseContext responseContext) {
        Map<String, Object> commands = exchange.getIn().getHeaders();
        for (String key : commands.keySet()) {
            LOGGER.debug("Key: {} -> value: {}", key, commands.get(key));
        }
        if (responseContext != null) {
            exchange.getOut().setBody(responseContext.getResponse());
            exchange.getOut().setHeader(CONTENT_TYPE, responseContext.getContentType());
            exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, (responseContext.getErrorCode() == null ? 200 : responseContext.getErrorCode()));
        } else {
            exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
        }
    }

}
