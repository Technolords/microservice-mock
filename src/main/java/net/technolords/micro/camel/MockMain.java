package net.technolords.micro.camel;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.technolords.micro.camel.listener.MockMainListener;
import net.technolords.micro.camel.route.EurekaRenewalRoute;
import net.technolords.micro.camel.route.MockRoute;
import net.technolords.micro.registry.MockRegistry;

public class MockMain extends Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockMain.class);

    /**
     * Default constructor which registers the start up properties.
     */
    public MockMain() {
        MockRegistry.registerPropertiesInRegistry(this);
    }

    /**
     * This method is invoked before the service is started, and performs some initialization steps. These
     * steps include:
     *
     * - Creation of beans, as well as adding these to the registry.
     * - Creation of the Camel routes, as well as adding these to the CamelContext.
     *
     * @throws JAXBException
     *  When creation the configuration bean fails.
     * @throws IOException
     *  When creation the configuration bean fails.
     * @throws SAXException
     *  When creation the configuration bean fails.
     */
    @Override
    public void beforeStart() throws JAXBException, IOException, SAXException {
        LOGGER.debug("Before start called...");
        MockRegistry.registerBeansInRegistryBeforeStart();
        super.addMainListener(new MockMainListener());
        super.addRouteBuilder(new MockRoute());
        if (MockRegistry.findRegistrationManager().renewalRequired()) {
            LOGGER.info("Adding renewal route...");
            super.addRouteBuilder(new EurekaRenewalRoute());
        }
    }

    /**
     * This method is invoked after the service is started, and logs a confirmation message.
     */
    @Override
    public void afterStart() {
        LOGGER.debug("After start called...");
        MockRegistry.registerBeansInRegistryAfterStart();
        LOGGER.info("Mock service started ({}), use CTRL-C to terminate JVM", MockRegistry.findBuildMetaData());
    }

    /**
     * Auxiliary method to start the micro service.
     *
     * @throws Exception
     *  When the micro service fails.
     */
    public void startService() throws Exception {
        super.run();
    }

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
        LOGGER.info("About to start the Mock service...");
        MockMain mockedRestService = new MockMain();
        mockedRestService.startService();
    }
}
