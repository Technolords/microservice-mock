package net.technolords.micro.camel.processor;

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
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.registry.MockRegistry;

public class ResponseProcessor implements Processor {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String CONTENT_TYPE = "Content-Type";
    private ConfigurationManager configurationManager;

    public ResponseProcessor() {
        this.configurationManager = MockRegistry.findConfigurationManager();
    }

    // Supporting test scenario's
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
            case ConfigurationManager.HTTP_PUT:
                responseContext = this.handlePutRequest(exchange);
                break;
            case ConfigurationManager.HTTP_PATCH:
                responseContext = this.handlePatchRequest(exchange);
                break;
            case ConfigurationManager.HTTP_DELETE:
                responseContext = this.handleDeleteRequest(exchange);
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
        // Example: "CamelHttpUri" -> "/mock/get"
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        if (requestURI.equals("/mock/cmd")) {
            return CommandManager.executeCommand(exchange);
        } else {
            // Example: "CamelHttpQuery" -> "key1=11&key2=12"
            String requestParameters = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
            return this.configurationManager.findResponseForGetOperation(requestURI, requestParameters);
        }
    }

    /**
     * Auxiliary method that delegates the request towards the configuration manager, based on the request URI.
     *
     * @param exchange
     *  The Camel exchange associated with the DELETE request.
     * @return
     *  A ResponseContext (or null when there was no match (this will trigger a 404)).
     *
     * @throws InterruptedException
     *  When the response with delay got interrupted.
     * @throws IOException
     *  When reading the response data failed.
     */
    private ResponseContext handleDeleteRequest(Exchange exchange) throws IOException, InterruptedException {
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String requestParameters = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        return this.configurationManager.findResponseForDeleteOperation(requestURI, requestParameters);
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
     *  When the Xpath failed, based on an XML body.
     * @throws IOException
     *  When reading the response data failed.
     */
    private ResponseContext handlePostRequest(Exchange exchange) throws InterruptedException, XPathExpressionException, IOException {
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String body = exchange.getIn().getBody(String.class);
        String discriminator = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
        return this.configurationManager.findResponseForPostOperation(requestURI, body, discriminator);
    }

    /**
     * Auxiliary method that delegates the request towards the configuration manager, based on the request URI as
     * well as the body associated with the PUT request.
     *
     * @param exchange
     *  The Camel exchange associated with the PUT request.
     * @return
     *  A RespinseContext (or null when there was no match (this will trigger a 404))
     *
     * @throws XPathExpressionException
     *  When the Xpath failed, based on an XML body.
     * @throws IOException
     *  When reading the response data failed.
     * @throws InterruptedException
     *  When the response with delay got interrupted.
     */
    private ResponseContext handlePutRequest(Exchange exchange) throws XPathExpressionException, IOException, InterruptedException {
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String body = exchange.getIn().getBody(String.class);
        String discriminator = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
        return this.configurationManager.findResponseForPutOperation(requestURI, body, discriminator);
    }

    /**
     * Auxiliary method that delegates the request towards the configuration manager, based on the request URI as
     * well as the body associated with the PATCH request.
     *
     * @param exchange
     *  The Camel exchange associated with the PATCH request.
     * @return
     *  A RespinseContext (or null when there was no match (this will trigger a 404))
     *
     * @throws XPathExpressionException
     *  When the Xpath failed, based on an XML body.
     * @throws IOException
     *  When reading the response data failed.
     * @throws InterruptedException
     *  When the response with delay got interrupted.
     */
    private ResponseContext handlePatchRequest(Exchange exchange) throws XPathExpressionException, IOException, InterruptedException {
        String requestURI = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String body = exchange.getIn().getBody(String.class);
        String discriminator = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
        return this.configurationManager.findResponseForPatchOperation(requestURI, body, discriminator);
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
