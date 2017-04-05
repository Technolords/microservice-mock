package net.technolords.micro.camel.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.test.PathSupport;

public class ResponseProcessorTest  {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATASET_FOR_CONFIGURATIONS = "dataSetMockExpectation";

    @DataProvider(name = DATASET_FOR_CONFIGURATIONS)
    public Object[][] dataSetMock() {
        return new Object[][] {
            { "config-for-ResponseProcessorTest.xml", ConfigurationManager.HTTP_POST, "/mock/post", "post-1-for-ResponseProcessorTest.xml", "post-1-for-ResponseProcessorTest.txt"},
            { "config-for-ResponseProcessorTest.xml", ConfigurationManager.HTTP_GET, "/mock/get", null, "get-1-for-ResponseProcessorTest.txt"}
        };
    }

    @Test (dataProvider = DATASET_FOR_CONFIGURATIONS)
    public void testMockResponses(final String configFile, final String method, final String uri, final String requestFile, final String responseFile) throws Exception {
        LOGGER.info("About to test with request file: {}, and expected response file: {}", requestFile, responseFile);

        // Initialize with configuration
        Path pathToConfigFile = FileSystems.getDefault().getPath(PathSupport.getTestConfigResourcesForMockAsString() + File.separator + configFile);
        Assert.assertTrue(Files.exists(pathToConfigFile));
        ConfigurationManager configurationManager = new ConfigurationManager(pathToConfigFile.toString(), null);
        ResponseProcessor responseProcessor = new ResponseProcessor(configurationManager);

        // Create and send request
        Exchange exchange = this.generateExchange(method, uri, requestFile);
        responseProcessor.process(exchange);

        // Assert response
        Path pathToResponseFile = PathSupport.getPathToTestDataForResponseResources();
        Path pathToResource = Paths.get(pathToResponseFile.toString(), responseFile);
        Assert.assertTrue(Files.exists(pathToResource));
        String actualResponse = exchange.getOut().getBody(String.class);
        String expectedResponse = new String(Files.readAllBytes(pathToResource));
        Assert.assertEquals(actualResponse, expectedResponse);
    }

    private Exchange generateExchange(final String method, final String uri, final String requestFile) throws IOException {
        Path pathToRequestFile = PathSupport.getPathToTestDataForRequestResources();
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        if (requestFile != null) {
            Path pathToResource = Paths.get(pathToRequestFile.toString(), requestFile);
            Assert.assertTrue(Files.exists(pathToResource));
            String requestContent = new String(Files.readAllBytes(pathToResource));
            exchange.getIn().setBody(requestContent);
        }
        return exchange;
    }

}