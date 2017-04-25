package net.technolords.micro.input;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.test.PathSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationSelectorTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSelectorTest.class);
    private static final String DATA_SET_FOR_TEST_CONFIGURATION_SELECTION = "dataSetForTestConfigurationSelection";
    private Map<String, Configuration> getConfigurations = new HashMap<>();
    private Map<String, Configuration> postConfigurations = new HashMap<>();
    private ConfigurationSelector configurationSelector = new ConfigurationSelector();
    public static final String HTTP_POST = "POST";

    @DataProvider(name = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public Object[][] dataSetConfigs(){
        return new Object[][] {
                { "\\config-for-ConfigSelectorTest.xml", 5},
                { "\\bigDataConfig.xml", 1 }
        };
    }

    @Test(dataProvider = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public void testConfigurationSelection(String resourceFile, int expectedConfigurationsSize) throws IOException, JAXBException, SAXException, InterruptedException {
        final String CONFIG_LOCATION = PathSupport.getPathToTestConfigForMockResources().toString() + resourceFile;
        final String DATA_LOCATION = PathSupport.getTestDataResourcesForResponse();
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations defaultConfigurations = configurationManager.getConfigurations();
        defaultConfigurations.getConfigurations().clear();
        Assert.assertTrue(defaultConfigurations.getConfigurations().size() == 0);

        configurationManager = new ConfigurationManager(CONFIG_LOCATION, DATA_LOCATION);
        int loadConfigurationsSize = configurationManager.getConfigurations().getConfigurations().size();
        LOGGER.info("Loaded Configurations = {}",loadConfigurationsSize);
        Assert.assertEquals(loadConfigurationsSize,expectedConfigurationsSize);

        for(Configuration configuration : configurationManager.getConfigurations().getConfigurations()){
            if (HTTP_POST.equals(configuration.getType().toUpperCase())) {
                // Add resource to post configuration group
                this.postConfigurations.put(configuration.getUrl(), configuration);
                Configuration actualPostConfiguration = configurationSelector.findMatchingConfiguration(configuration.getUrl(), this.postConfigurations);
                Assert.assertEquals(actualPostConfiguration.getUrl(), configuration.getUrl());
            } else {
                // Add resource to get configuration group
                this.getConfigurations.put(configuration.getUrl(), configuration);
                Configuration actualGetConfiguration = configurationSelector.findMatchingConfiguration(configuration.getUrl(),this.getConfigurations);
                Assert.assertEquals(actualGetConfiguration.getUrl(), configuration.getUrl());
            }
        }
    }
}