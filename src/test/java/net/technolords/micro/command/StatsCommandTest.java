package net.technolords.micro.command;

import java.util.Properties;

import net.technolords.micro.RouteTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.apache.camel.testng.AvailablePortFinder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import net.technolords.micro.camel.RestServiceRoute;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.registry.MockRegistry;

public class StatsCommandTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommandTest.class);
    private Main main;
    private ProducerTemplate producerTemplate;
    private String availablePort;
    private static StatisticsHandler statisticsHandler;
    private static final String JETTY_TEST_ENDPOINT = "http://0.0.0.0:9090/";

    @BeforeSuite
    public void findAvailablePortNumber() {
        LOGGER.info("BeforeSuite called...");
        availablePort = String.valueOf(AvailablePortFinder.getNextAvailable(10000));
        LOGGER.info("Found port: {}", availablePort);
    }

    @BeforeTest
    public void setUp() throws Exception {
        LOGGER.info("BeforeTest called...");
        main = new Main();
        MockRegistry.registerPropertiesInRegistry(main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        Properties properties = MockRegistry.findProperties();
        properties.put("port", availablePort);
        main.addRouteBuilder(new RestServiceRoute());
        LOGGER.info("Added Route, main started: {}", main.isStarted());
        main.start();
        MockRegistry.registerBeansInRegistryAfterStart();
        producerTemplate = main.getCamelTemplate();
    }

    @Test
    public void testStatsCommand() throws Exception{
        LOGGER.info("About to send stats command, total routes: {}", producerTemplate.getCamelContext().getRoutes().size());
        Exchange response = producerTemplate.request("jetty:http://localhost:" + availablePort + "/mock/cmd", exchange -> {
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
            exchange.getIn().setHeader("stats", "html");
        });
        StatisticsHandler statisticsHandler = MockRegistry.findStatisticsHandler();
        Assert.assertTrue(statisticsHandler.getRequests() == 1);
        String html = response.getOut().getBody(String.class);
        LOGGER.info("Got html: {}", html);
    }

    @Test
    public void testStatsCommand2() throws Exception{
        setUpToStartServer();

        String uri = "mock/cmd?stats=html";
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(JETTY_TEST_ENDPOINT);
        request.addHeader("URI",uri);
        CloseableHttpResponse response = client.execute(request);
        LOGGER.info("Response code: ", response);
        statisticsHandler = MockRegistry.findStatisticsHandler();
        Assert.assertTrue(statisticsHandler.getRequests() == 1);
    }
}