package net.technolords.micro.command;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CommandManagerTest extends RouteTestSupport{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATASET_FOR_COMMAND_MANAGER = "datasetCommands";
    ResponseContext responseContext = new ResponseContext();
    CommandManager commandManager = new CommandManager();


    @DataProvider(name = DATASET_FOR_COMMAND_MANAGER)
    public Object[][] dataset() {
        return new Object[][] {
                {"unknown","text/plain","501", "Currently not supported"},
                {"stop","text/plain",null,"Stopping the mock.."},
//                {"config","application/xml",null,"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:configurations " +
//                        "xmlns:ns2=\"http://xsd.technolords.net\"><configuration type=\"GET\" url=\"/mock/get\"><resource delay=\"0\" " +
//                        "error-rate=\"0\">mock/sample-get.json</resource></configuration><configuration type=\"GET\" url=\"/mock/*/get\">" +
//                        "<resource delay=\"0\" error-rate=\"0\">mock/sample-get.json</resource></configuration><configuration type=\"GET\" url=\"/mock/1/get\"><resource delay=\"0\" " +
//                        "error-rate=\"0\">mock/sample-get.json</resource></configuration><configuration type=\"POST\" url=\"/mock/post\">" +
//                        "<namespaces><namespace prefix=\"technolords\">urn:some:reference:1.0</namespace></namespaces><resource-groups><resource-group>" +
//                        "<resource delay=\"0\" error-rate=\"0\">mock/sample-post1.json</resource><xpath>/technolords:sample/technolords:message[@id = '1']</xpath></resource-group><resource-group><resource delay=\"10000\" " +
//                        "error-rate=\"0\">mock/sample-post2.json</resource><xpath>/technolords:sample/technolords:message[@id = '2']</xpath></resource-group><resource-group><resource delay=\"0\" " +
//                        "error-code=\"206\" error-rate=\"50\">mock/sample-post3.json</resource><xpath>/technolords:sample/technolords:message[@id = '3']</xpath></resource-group><resource-group><resource " +
//                        "content-type=\"text/plain\" delay=\"0\" error-rate=\"0\">mock/sample-post4.txt</resource><xpath>/technolords:sample/technolords:message[@id = '4']</xpath>" +
//                        "</resource-group></resource-groups></configuration></ns2:configurations>"},
                {"log","text/plain",null,"Log level changed to INFO"},
                {"reset","text/plain",null,"Statistics has been reset"}
        };
    }

    @Test(dataProvider = DATASET_FOR_COMMAND_MANAGER)
    public void testCommandManager(final String command, final String contentType, final String errorCode, final String response) throws Exception{
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
//        StatisticsHandler statisticsHandler = MockRegistry.findStatisticsHandler();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
        exchange.getIn().setHeader(Exchange.HTTP_URI, "/mock/get");
        exchange.getIn().setHeader(command,contentType);

        responseContext = commandManager.executeCommand(exchange);
        LOGGER.info("Response: {}" , responseContext.getResponse());
        LOGGER.info("Content Type: {}" , responseContext.getContentType());
        LOGGER.info("Error code: {}" , responseContext.getErrorCode());

        Assert.assertEquals(responseContext.getResponse(), response);
        Assert.assertEquals(responseContext.getErrorCode(), errorCode);
        Assert.assertEquals(responseContext.getContentType(), contentType);
    }
}