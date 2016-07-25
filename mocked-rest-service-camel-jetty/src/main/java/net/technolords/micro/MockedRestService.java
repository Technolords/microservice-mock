package net.technolords.micro;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.route.RestServiceRoute;

/**
 * Created by Technolords on 2016-Jun-23.
 */
public class MockedRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockedRestService.class);
    private static final String PROP_PORT = "port";
    private static final String PROP_CONFIG = "config";
    private static final String DEFAULT_PORT = "9090";
    private Main main;

    public void startService(String port) throws Exception {
        this.main = new Main();
        this.main.addRouteBuilder(new RestServiceRoute(port));
        LOGGER.info("Route created, use CTRL-C to terminate JVM");
        this.main.run();
    }

    // TODO: extend from main, and provide @Beforestart and @Afterstart
    // see also: http://www.javadoc.io/doc/org.apache.camel/camel-core/2.17.1

    public static void main(String[] args) throws Exception {
        LOGGER.info("About to start the mocked service...");
        MockedRestService mockedRestService = new MockedRestService();
        String port = DEFAULT_PORT;
        if (System.getProperty(PROP_PORT) != null) {
            LOGGER.debug("Configured Port: {}", System.getProperty("port"));
            port = System.getProperty(PROP_PORT);
        }
        mockedRestService.startService(port);
    }
}
