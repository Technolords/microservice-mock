package net.technolords.micro.config;

import static java.nio.file.StandardOpenOption.READ;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);
    public static final String PROP_PROPS = "props";
    public static final String PROP_PORT = "port";
    public static final String PROP_CONFIG = "config";
    public static final String PROP_DATA = "data";
    private static final String DEFAULT_PORT = "9090";

    /**
     * Auxiliary method that creates map of key value pairs (representing the properties) by looking at the provided
     * system properties. Part of these properties can be a reference to a properties files.
     *
     * In case both are specified, that is a properties file containing a definition of a property as well as a provided
     * system property the latter wins. As result this method will merge the values.
     *
     * In case no property file is specified, this method will provide the properties containing the
     * provided system properties only.
     *
     * @return
     */
    public static Properties extractProperties() {
        Properties properties = loadProperties(System.getProperty(PROP_PROPS));
        if (System.getProperty(PROP_PORT) != null) {
            LOGGER.debug("Configured Port: {}", System.getProperty(PROP_PORT));
        }
        properties.put(PROP_PORT, System.getProperty(PROP_PORT, DEFAULT_PORT));
        if (System.getProperty(PROP_CONFIG) != null) {
            LOGGER.debug("Configured Config: {}", System.getProperty(PROP_CONFIG));
            properties.put(PROP_CONFIG, System.getProperty(PROP_CONFIG));
        }
        if (System.getProperty(PROP_DATA) != null) {
            LOGGER.debug("Configured data: {}", System.getProperty(PROP_DATA));
            properties.put(PROP_DATA, System.getProperty(PROP_DATA));
        }
        for (Object key : properties.keySet()) {
            LOGGER.info("Key: {} -> value: {}", key, properties.get(key));
        }
        return properties;
    }

    /**
     * Auxiliary method to load the properties.
     *
     * @param pathToPropertiesFile
     *  The path to the properties file.
     *
     * @return
     *  The properties.
     */
    private static Properties loadProperties(String pathToPropertiesFile) {
        Properties properties = new Properties();
        try {
            if (pathToPropertiesFile == null) {
                return properties;
            }
            Path path = FileSystems.getDefault().getPath(pathToPropertiesFile);
            properties.load(Files.newInputStream(path, READ));
        } catch (IOException e) {
            LOGGER.warn("Unable to read properties -> ignoring values and using defaults", e);
        }
        return properties;
    }
}
