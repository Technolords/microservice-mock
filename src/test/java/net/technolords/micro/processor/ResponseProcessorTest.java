package net.technolords.micro.processor;


import junit.framework.Assert;
import net.technolords.micro.config.ConfigurationManager;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ResponseProcessorTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    String PATH_TO_CONFIG_FILE = "D:/commit2Learn/data/mockConfigurations/";
    String PATH_TO_REQUEST = "D:/commit2Learn/data/mockRequests/";
    String PATH_TO_RESPONSE = "D:/commit2Learn/data/mockResponses/";

    @DataProvider(name = "dataSetMockExpectation")
    public Object[][] dataSetMock(){
        return new Object[][] {
                {"configuration-test1.xml","sample-post1-request.xml", "sample-post1.txt."}
        };
    }

    @Test(dataProvider = "dataSetMockExpectation")
    public void testPostResponses(String configFile, String request, String expectedResponse) throws Exception {

//        String expectedResponse = "{\n" +
//                "  \"label \" : \"post-request1\",\n" +
//                "  \"data\" : \"this is also fun\"\n" +
//                "}";

        String requestContent = new String(Files.readAllBytes(Paths.get(PATH_TO_REQUEST + request)));
        String responseContent = new String(Files.readAllBytes(Paths.get(PATH_TO_RESPONSE + expectedResponse)));

        String pathToConfig = PATH_TO_CONFIG_FILE + configFile;
        ConfigurationManager configurationManager = new ConfigurationManager(pathToConfig, null);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getIn().setHeader(Exchange.HTTP_URI, "/mock/post");
        exchange.getIn().setBody(requestContent);

        ResponseProcessor responseProcessor = new ResponseProcessor(configurationManager);
        responseProcessor.process(exchange);

        //Assertions
        String actualResponse = exchange.getIn().getBody(String.class);
        Assert.assertEquals(actualResponse, expectedResponse);


    }


}