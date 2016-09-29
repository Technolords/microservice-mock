package net.technolords.micro.processor;

import net.technolords.micro.TestSupport;
import net.technolords.micro.config.ConfigurationManager;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResponseProcessorTest extends TestSupport{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @DataProvider(name = "dataSetMockExpectation")
    public Object[][] dataSetMock(){
        return new Object[][] {
                {"configuration-test1.xml","POST", "/mock/post", "sample-post1-request.xml", "sample-post1.txt."},
                {"configuration-test1.xml","GET", "/mock/get",null, "sample-get.txt"}
        };
    }

    @Test(dataProvider = "dataSetMockExpectation")
    public void testPostResponses(String configFile, String method, String URI, String request, String expectedResponse) throws Exception {
        // Create a path to the file
        Path pathToConfigFile = FileSystems.getDefault().getPath(getPathToDataFolder() + File.separator + "mockConfigurations" + File.separator);
        Path pathToRequestFile = FileSystems.getDefault().getPath(getPathToDataFolder() + File.separator + "mockRequests");
        Path pathToResponseFile = FileSystems.getDefault().getPath(getPathToDataFolder() + File.separator + "mockResponses");

        String responseContent = new String(Files.readAllBytes(Paths.get(pathToResponseFile + File.separator + expectedResponse)));


        String pathToConfig = pathToConfigFile + File.separator +configFile;
        ConfigurationManager configurationManager = new ConfigurationManager(pathToConfig, null);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, URI);

        if(request != null){
        String requestContent = new String(Files.readAllBytes(Paths.get(pathToRequestFile + File.separator+ request)));
        exchange.getIn().setBody(requestContent);
        }


        ResponseProcessor responseProcessor = new ResponseProcessor(configurationManager);
        responseProcessor.process(exchange);

        //Assertions
        String actualResponse = exchange.getIn().getBody(String.class);
        Assert.assertEquals(actualResponse, responseContent);


    }


}