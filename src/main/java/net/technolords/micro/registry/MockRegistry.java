package net.technolords.micro.registry;

import static net.technolords.micro.config.PropertiesManager.PROP_BUILD_DATE;
import static net.technolords.micro.config.PropertiesManager.PROP_BUILD_VERSION;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.camel.main.Main;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.config.PropertiesManager;
import net.technolords.micro.filter.InfoFilter;

/**
 * This class 'isolates' all the Registry interfacing with Camel, and basically serves as a centralized
 * way of implementation. By no means this class is intended to replace or implement a Registry. This class
 * in fact substitutes for a IOC solution (like Spring or Blueprint).
 */
public class MockRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockRegistry.class);
    private static final String BEAN_PROPERTIES = "props";
    private static final String BEAN_META_DATA_PROPERTIES = "propsMetaData";
    private static final String BEAN_METRICS = "metrics";
    private static final String BEAN_JETTY_SERVER = "jettyServer";
    private static final String BEAN_CONFIG = "config";
    private static final String BEAN_FILTER_INFO = "infoFilter";
    private static Main main;

    /**
     * Custom constructor with a reference of the main which is used to store the properties.
     *
     * @param mainReference
     *  A reference of the Main object.
     */
    public static void registerPropertiesInRegistry(Main mainReference) {
        main = mainReference;
        main.bind(BEAN_META_DATA_PROPERTIES, PropertiesManager.extractMetaData());
        main.bind(BEAN_PROPERTIES, PropertiesManager.extractProperties());
    }

    /**
     * Auxiliary method to register beans to the Main component, with the purpose of supporting lookup mechanism
     * in case references of the beans are required. The latter typically occurs at runtime, but also in cases
     * of lazy initialization. Note that this micro service is not using any dependency injection framework.
     *
     * @throws JAXBException
     *  When creation the configuration bean fails.
     * @throws IOException
     *  When creation the configuration bean fails.
     * @throws SAXException
     *  When creation the configuration bean fails.
     */
    public static void registerBeansInRegistryBeforeStart() throws JAXBException, IOException, SAXException {
        main.bind(BEAN_METRICS, new StatisticsHandler());
        main.bind(BEAN_CONFIG, new ConfigurationManager(findConfiguredConfig(), findConfiguredData()));
        main.bind(BEAN_FILTER_INFO, new InfoFilter());
        LOGGER.info("Beans added to the registry...");
    }

    /**
     * Auxiliary method to register beans to the Main component, but after the Main has started. Typically the
     * underlying Server is also started and instantiated.
     */
    public static void registerBeansInRegistryAfterStart() {
        StatisticsHandler statisticsHandler = findStatisticsHandler();
        Server server = statisticsHandler.getServer();
        main.bind(BEAN_JETTY_SERVER, server);
        InfoFilter.registerFilterDirectlyWithServer(server);
    }

    /**
     * Auxiliary method to perform a lookup of the ConfigurationManager.
     *
     * @return
     *  A reference of the ConfigurationManager.
     */
    public static ConfigurationManager findConfigurationManager() {
        return main.lookup(BEAN_CONFIG, ConfigurationManager.class);
    }

    /**
     * Auxiliary method to perform a lookup of the StatisticsHandler.
     *
     * @return
     *  A reference of the StatisticsHandler.
     */
    public static StatisticsHandler findStatisticsHandler() {
        return main.lookup(BEAN_METRICS, StatisticsHandler.class);
    }

    /**
     * Auxiliary method to perform a lookup of the Jetty Server.
     *
     * @return
     *  A reference of the Jetty Server.
     */
    public static Server findJettyServer() {
        return main.lookup(BEAN_JETTY_SERVER, Server.class);
    }

    /**
     * Auxiliary method to perform a lookup of the InfoFilter.
     *
     * @return
     *  A reference of the InfoFilter.
     */
    public static InfoFilter findInfoFilter() {
        return main.lookup(BEAN_FILTER_INFO, InfoFilter.class);
    }

    public static Properties findProperties() {
        return main.lookup(BEAN_PROPERTIES, Properties.class);
    }

    public static String findConfiguredPort() {
        return (String) findProperties().get(PropertiesManager.PROP_PORT);
    }

    public static String findConfiguredConfig() {
        return (String) findProperties().get(PropertiesManager.PROP_CONFIG);
    }

    public static String findConfiguredData() {
        return (String) findProperties().get(PropertiesManager.PROP_DATA);
    }

    public static String findBuildMetaData() {
        Properties properties = main.lookup(BEAN_META_DATA_PROPERTIES, Properties.class);
        StringBuilder buffer = new StringBuilder();
        buffer.append(properties.get(PROP_BUILD_VERSION));
        buffer.append(" ");
        buffer.append(properties.get(PROP_BUILD_DATE));
        return buffer.toString();
    }

}
