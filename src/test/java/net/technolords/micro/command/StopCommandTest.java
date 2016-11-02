package net.technolords.micro.command;

import net.technolords.micro.MockedRestService;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.route.RestServiceRoute;
import org.apache.camel.Exchange;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.testng.CamelTestSupport;
import org.slf4j.Logger;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class StopCommandTest extends CamelTestSupport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testStopCommand() throws Exception{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "/mock/cmd?stop=now";
        Exchange exchange;

//        MockedRestService mockedRestService = new MockedRestService();
//        mockedRestService.start();
//
//        Assert.assertTrue(ServiceStatus.Started.isStarted());

//       RouteBuilder routeBuilder = new RouteBuilder() {
//           @Override
//           public void configure() throws Exception {
//               errorHandler(deadLetterChannel("mock:error"));
//
//               from("direct:a").to("direct:b");
//           }
//       };


        exchange = this.generateExchange(method, uri);

        Assert.assertEquals(exchange.getContext().getStatus(), ServiceStatus.Stopped);
    }

    private Exchange generateExchange(String method, String uri) throws Exception {
        method = ConfigurationManager.HTTP_GET;
        uri = "/mock/cmd?stop=now";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        context().addRoutes(new RestServiceRoute());
        context().start();

        Assert.assertTrue(ServiceStatus.Started.isStarted());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        return exchange;
    }
}