package net.technolords.micro.command;

import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;
import org.apache.camel.testng.AvailablePortFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.route.RestServiceRoute;

public class StopCommandTest  {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
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
        main = new Main();
        MockRegistry.registerPropertiesInRegistry(main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        Properties properties = MockRegistry.findProperties();
        properties.put("port", availablePort);
        main.addRouteBuilder(new RestServiceRoute());
        LOGGER.info("Added Route, main started: {}", main.isStarted());
        main.start();
        producerTemplate = main.getCamelTemplate();
    }

    @Test
    public void testStopCommand() throws Exception{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "/mock/cmd?stop=now";
        Exchange exchange = this.generateExchange(method, uri);
        LOGGER.info("About to stop, current current context: {} -> started: {}", producerTemplate.getCamelContext().getName(), main.isStarted());
        producerTemplate.send("direct:main", exchange);
        Thread.sleep(1000L);
        LOGGER.info("Exchange send, current context: {} -> started: {}", producerTemplate.getCamelContext().getName(), exchange.getContext().getStatus());
        Assert.assertEquals(exchange.getContext().getStatus(), ServiceStatus.Stopped);
    }

    private Exchange generateExchange(String method, String uri) throws Exception {
        method = ConfigurationManager.HTTP_GET;
        uri = "/mock/cmd";
        Exchange exchange = new DefaultExchange(producerTemplate.getCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        exchange.getIn().setHeader("stop", "now");
        return exchange;
    }
}