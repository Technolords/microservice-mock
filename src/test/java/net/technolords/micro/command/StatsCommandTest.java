package net.technolords.micro.command;

import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.apache.camel.testng.AvailablePortFinder;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.route.RestServiceRoute;

public class StatsCommandTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommandTest.class);
    private Main main;
    private ProducerTemplate producerTemplate;
    private String availablePort;

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

}