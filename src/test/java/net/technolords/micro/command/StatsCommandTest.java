package net.technolords.micro.command;

import org.apache.camel.Exchange;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.registry.MockRegistry;

public class StatsCommandTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommandTest.class);

    @Test (description = "Test stats command with actual endpoint")
    public void testStatsCommand() throws Exception{
        LOGGER.info("About to send stats command, total routes: {}", super.getProducerTemplate().getCamelContext().getRoutes().size());
        Exchange response = super.getProducerTemplate().request("jetty:http://localhost:" + super.getAvailablePort() + "/mock/cmd", exchange -> {
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
            exchange.getIn().setHeader("stats", "html");
        });
        StatisticsHandler statisticsHandler = MockRegistry.findStatisticsHandler();
        Assert.assertTrue(statisticsHandler.getRequests() == 1);
        String html = response.getOut().getBody(String.class);
        LOGGER.info("Got html: {}", html);
    }
}