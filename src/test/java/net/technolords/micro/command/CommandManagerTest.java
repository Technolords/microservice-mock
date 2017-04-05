package net.technolords.micro.command;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.test.PathSupport;

public class CommandManagerTest extends RouteTestSupport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATA_SET_FOR_COMMAND_MANAGER = "dataSetForCommandManager";
    private CommandManager commandManager;

    @BeforeTest(description = "Initialize XMLUnit")
    public void initializeXMLUnit() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
    }

    @BeforeMethod
    public void initializeCommandManager() {
        this.commandManager = new CommandManager();
    }

    /**
     * Auxiliary method to declare a data set to support testing of the CommandManager. An entry is specified
     * with four elements, each meaning:
     *
     *  [0] : The command
     *  [1] : The expected context type
     *  [2] : The expected response code
     *  [3] : The expected message
     *
     * @return
     *  The data set.
     */
    @DataProvider (name = DATA_SET_FOR_COMMAND_MANAGER)
    public Object[][] dataSetForCommandManager() throws IOException {
        return new Object[][] {
                { "unknown", ResponseContext.PLAIN_TEXT_CONTENT_TYPE, "501", "Currently not supported" },
                { "stop", ResponseContext.PLAIN_TEXT_CONTENT_TYPE, null, "Stopping the mock.." },
                { "config", ResponseContext.XML_CONTENT_TYPE, null, expectedResponse() },
                { "log", ResponseContext.PLAIN_TEXT_CONTENT_TYPE, null, "Log level changed to INFO" },
                { "reset", ResponseContext.PLAIN_TEXT_CONTENT_TYPE, null, "Statistics has been reset" },
        };
    }

    private static String expectedResponse() throws IOException {
        Path pathToRequestFile = PathSupport.getPathToTestConfigForMockResources();
        Path pathToResource = Paths.get(pathToRequestFile.toString(), "config-for-CommandManagerTest.xml");
        Assert.assertTrue(Files.exists(pathToResource));
        return new String(Files.readAllBytes(pathToResource));
    }

    @Test (dataProvider = DATA_SET_FOR_COMMAND_MANAGER)
    public void testCommandManager(final String command, final String contentType, final String errorCode, final String response) throws Exception {
        // Prepare
        Exchange exchange = new DefaultExchange(super.context());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
        exchange.getIn().setHeader(Exchange.HTTP_URI, "/mock/get");
        exchange.getIn().setHeader(command, contentType);

        // Execute
        ResponseContext responseContext = this.commandManager.executeCommand(exchange);
        LOGGER.info("Response: {}" , responseContext.getResponse());
        LOGGER.info("Content Type: {}" , responseContext.getContentType());
        LOGGER.info("Error code: {}" , responseContext.getErrorCode());

        // Assert (note we cannot use String compare when we have XML responses)
        switch (contentType) {
            case ResponseContext.XML_CONTENT_TYPE:
                assertXMLEqual(response, responseContext.getResponse());
                break;
            case ResponseContext.PLAIN_TEXT_CONTENT_TYPE:
                Assert.assertEquals(responseContext.getContentType(), contentType);
                break;
            default:
                Assert.fail("Unsupported contextType: " + contentType + " -> unclear how to compare response versus expected...");
        }
        Assert.assertEquals(responseContext.getErrorCode(), errorCode);
        Assert.assertEquals(responseContext.getContentType(), contentType);
    }
}