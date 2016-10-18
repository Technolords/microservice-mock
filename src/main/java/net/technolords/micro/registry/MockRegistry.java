package net.technolords.micro.registry;

import static net.technolords.micro.MockedRestService.PROP_CONFIG;
import static net.technolords.micro.MockedRestService.PROP_DATA;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.camel.main.Main;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.technolords.micro.config.ConfigurationManager;

/**
 * This class 'isolates' all the Registry interfacing with Camel, and basically serves as a centralized
 * way of implementation. By no means this class is intended to replace or implement a Registry.
 */
public class MockRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockRegistry.class);
    private static final String METRICS = "metrics";
    private static final String CONFIG = "config";
    private static Main main;

    /**
     * Auxiliary method to register beans to the Main component, with the purpose of supporting lookup mechanism
     * in case references of the beans are required. The latter typically occurs at runtime, but also in cases
     * of lazy initialization. Note that this micro service is not using any dependency injection framework.
     *
     * @param mainReference
     *  A refefence of the Main object.
     *
     * @throws JAXBException
     *  When creation the configuration bean fails.
     * @throws IOException
     *  When creation the configuration bean fails.
     * @throws SAXException
     *  When creation the configuration bean fails.
     */
    public static void registerBeansInRegistry(Main mainReference) throws JAXBException, IOException, SAXException {
        main = mainReference;
        main.bind(METRICS, new StatisticsHandler());
        main.bind(CONFIG, new ConfigurationManager(System.getProperty(PROP_CONFIG), System.getProperty(PROP_DATA)));
        LOGGER.info("Beans added to the registry...");
    }

    /**
     * Auxiliary method to perform a lookup of the ConfigurationManager.
     *
     * @return
     *  A reference of the ConfigurationManager.
     */
    public static ConfigurationManager findConfigurationManager() {
        return main.lookup(CONFIG, ConfigurationManager.class);
    }

    /**
     * Auxiliary method to perform a lookup of the StatisticsHandler.
     *
     * @return
     *  A reference of the StatisticsHandler.
     */
    public static StatisticsHandler findStatisticsHandler() {
        return main.lookup(METRICS, StatisticsHandler.class);
    }

}
