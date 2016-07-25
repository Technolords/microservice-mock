package net.technolords.micro.config;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Technolords on 2016-Jul-21.
 */
public class ConfigurationManagerTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testInitializationOfConfiguration() throws JAXBException {
        ConfigurationManager configurationManager = new ConfigurationManager(null);
        Assert.assertNotNull(configurationManager, "Expected the configuration manager to be initialized...");
        LOGGER.debug("Test of initialization of config completed, no errors...");
    }
}