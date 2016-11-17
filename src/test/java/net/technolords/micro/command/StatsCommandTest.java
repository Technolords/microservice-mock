package net.technolords.micro.command;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


public class StatsCommandTest extends RouteTestSupport{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommandTest.class);
    private static StatisticsHandler statisticsHandler;
    private static final String JETTY_TEST_ENDPOINT = "http://0.0.0.0:9090/";

    @Test
    public void testStatsCommand() throws Exception{
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