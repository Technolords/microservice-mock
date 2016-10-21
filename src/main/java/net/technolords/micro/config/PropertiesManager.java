package net.technolords.micro.config;

import java.util.Properties;

public class PropertiesManager {
    public static final String PROP_PORT = "port";
    public static final String PROP_CONFIG = "config";
    public static final String PROP_DATA = "data";
    private static final String DEFAULT_PORT = "9090";

    /**
     * Auxiliary method that creates properties by looking at the provided system properties.
     * Part of these properties can be a reference to a properties files. In case both are specified,
     * that is a properties file containing a definition of a property as well as a provided system property
     * the latter wins. As result this method will merge the values.
     *
     * In case no property file is specified, this method will provide the properties containing the
     * provided system properties only.
     *
     * @param args
     * @return
     */
    public static Properties extractProperties(String[] args) {
        return null;
    }
}
