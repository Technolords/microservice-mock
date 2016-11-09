package net.technolords.micro.command;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.processor.ResponseContext;
import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.route.RestServiceRoute;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;
import org.apache.camel.testng.CamelTestSupport;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class StatsCommandTest extends CamelTestSupport{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommandTest.class);
    private Main main;
    private ProducerTemplate producerTemplate;
    private static StatisticsHandler statisticsHandler;

    @BeforeTest
    public void setUp() throws Exception {
        main = new Main();
        MockRegistry.registerPropertiesInRegistry(main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        main.addRouteBuilder(new RestServiceRoute());
        LOGGER.info("Added Route, main started: {}", main.isStarted());
        main.start();
        producerTemplate = main.getCamelTemplate();
    }

    @Test
    public void testStatsCommand() throws Exception{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "mock/cmd?stats=html";
        Exchange exchange;
        exchange = this.generateExchange(method, uri);
        producerTemplate.send(RestServiceRoute.generateJettyEndpoint(),exchange);
        StatisticsHandler statisticsHandler = MockRegistry.findStatisticsHandler();
        Assert.assertTrue(statisticsHandler.getRequests() == 1);
    }

    private Exchange generateExchange(String method, String uri) throws Exception {
        method = ConfigurationManager.HTTP_GET;
        uri = "mock/cmd?stats=html";
        Exchange exchange = new DefaultExchange(producerTemplate.getCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        return exchange;
    }
}