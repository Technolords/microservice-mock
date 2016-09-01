package net.technolords.micro;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.route.RestServiceRoute;

/**
 * Created by Technolords on 2016-Jun-23.
 */
public class MockedRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockedRestService.class);
    private static final String PROP_PORT = "port";
    private static final String PROP_CONFIG = "config";
    private static final String PROP_DATA = "data";
    private static final String DEFAULT_PORT = "9090";
    private Main main;

    /**
     * Auxiliary method to start the micro service.
     *
     * @param port
     *  The port associated with the micro service.
     *
     * @throws Exception
     *  When the micro service fails.
     */
    public void startService(String port) throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager(System.getProperty(PROP_CONFIG), System.getProperty(PROP_DATA));
        this.main = new Main();
        this.main.addRouteBuilder(new RestServiceRoute(port, configurationManager));
        LOGGER.info("Route created, use CTRL-C to terminate JVM");
        this.main.run();
    }

    // TODO: extend from main, and provide @Beforestart and @Afterstart
    // see also: http://www.javadoc.io/doc/org.apache.camel/camel-core/2.17.1

    /**
     * The main executable.
     *
     * @param args
     *  The arguments.
     *
     * @throws Exception
     *  When the program fails.
     */
    public static void main(String[] args) throws Exception {
        LOGGER.info("About to start the mocked service...");
        MockedRestService mockedRestService = new MockedRestService();
        String port = DEFAULT_PORT;
        if (System.getProperty(PROP_PORT) != null) {
            LOGGER.debug("Configured Port: {}", System.getProperty(PROP_PORT));
            port = System.getProperty(PROP_PORT);
        }
        if (System.getProperty(PROP_CONFIG) != null) {
            LOGGER.debug("Configured Config: {}", System.getProperty(PROP_CONFIG));
        }
        if (System.getProperty(PROP_DATA) != null) {
            LOGGER.debug("Configured data: {}", System.getProperty(PROP_DATA));
        }
        mockedRestService.startService(port);
    }
}
