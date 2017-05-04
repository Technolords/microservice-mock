package net.technolords.micro.input;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.resource.SimpleResource;
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
    private ConfigurationSelector configurationSelector = new ConfigurationSelector();
    SimpleResource simpleResource = new SimpleResource();

    @DataProvider(name = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public Object[][] dataSetConfigs(){
        return new Object[][] {
                { "/mock/get",          getConfigs(),       getConfig1()},
                { "/mock/100/get",      getConfigs(),       wildCardConfig()},
                { "/mock/1/get",        getConfigs(),       getConfig2()},
                { "/mock/1/get/0/data", getConfigs(),       wildCardConfig2()},
                { "/mock/post",         postConfigs(),      postConfig1()}
//                {null,         postConfigs(),      null}
        };
    }

    @Test(dataProvider = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public void testConfigurationSelection(String url, Map<String ,Configuration> testConfigs, Configuration expectedConfiguration) throws IOException, JAXBException, SAXException, InterruptedException {
        for(String key : testConfigs.keySet()) {
            Configuration actualConfiguration = configurationSelector.findMatchingConfiguration(url, testConfigs);
            LOGGER.info("Equals: {}", actualConfiguration.equals(expectedConfiguration));
            Assert.assertEquals(actualConfiguration,expectedConfiguration);
        }
    }

    private Map<String,Configuration> getConfigs() {
        Map<String, Configuration> getConfigurations = new HashMap<>();
        getConfigurations.put("/mock/get",getConfig1());
        getConfigurations.put("/mock/*/get",wildCardConfig());
        getConfigurations.put("/mock/1/get",getConfig2());
        getConfigurations.put("/mock/*/get/*/data",wildCardConfig2());
        return getConfigurations;
    }

    private Map<String, Configuration> postConfigs() {
        Map<String, Configuration> postConfigurations = new HashMap<>();
        postConfigurations.put("/mock/post",postConfig1());
//        postConfigurations.put(null,emptyConfig());
        return postConfigurations;
    }

    private Configuration getConfig1(){
        Configuration configuration1 = new Configuration();
        configuration1.setUrl("/mock/get");
        configuration1.setType("GET");
        simpleResource.setResource("configManagerTest/getResponse1.txt");
        configuration1.setSimpleResource(simpleResource);
        return configuration1;
    }

    private Configuration wildCardConfig(){
        Configuration configuration2 = new Configuration();
        configuration2.setUrl("/mock/*/get");
        configuration2.setType("GET");
        simpleResource.setResource("configManagerTest/getResponse2.txt");
        configuration2.setSimpleResource(simpleResource);
        return configuration2;
    }

    private Configuration getConfig2(){
        Configuration configuration3 = new Configuration();
        configuration3.setUrl("/mock/1/get");
        configuration3.setType("GET");
        simpleResource.setResource("configManagerTest/getResponse1.txt");
        configuration3.setSimpleResource(simpleResource);
        return configuration3;
    }

    private Configuration wildCardConfig2(){
        Configuration configuration4 = new Configuration();
        configuration4.setUrl("/mock/*/get/*/data");
        configuration4.setType("GET");
        simpleResource.setResource("mock/sample-get.json");
        configuration4.setSimpleResource(simpleResource);
        return configuration4;
    }

    private Configuration postConfig1(){
        Configuration configuration5 = new Configuration();
        configuration5.setUrl("/mock/*/get/*/data");
        configuration5.setType("POST");
        simpleResource.setResource("mock/sample-post4.txt");
        configuration5.setSimpleResource(simpleResource);
        return configuration5;
    }

    private Configuration emptyConfig(){
        Configuration configuration6 = new Configuration();
        configuration6.setUrl(null);
        configuration6.setType(null);
        simpleResource.setResource(null);
        configuration6.setSimpleResource(simpleResource);
        return configuration6;
    }
}