package net.technolords.micro.command;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.domain.jaxb.Configuration;
import net.technolords.micro.domain.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.Exchange;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class ConfigCommandTest extends RouteTestSupport{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCommandTest.class);
    private static final String JETTY_TEST_ENDPOINT = "jetty:http://localhost:9090/";
    final String CONFIG_LOCATION = "src\\test\\resources\\data\\mockConfigurations";
    Path pathToRequestFile = FileSystems.getDefault().getPath(CONFIG_LOCATION);

    @BeforeTest
    public void setup(){
            XMLUnit.setIgnoreWhitespace(true);
            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setIgnoreComments(true);
    }

    @Test
    public void testConfigCommandWithDefault() throws Exception{
        LOGGER.info("About to send Config command, total routes: {}", getProducerTemplate().getCamelContext().getRoutes().size());
        String expectedConfig = new String(Files.readAllBytes(Paths.get(pathToRequestFile + File.separator + "default-configuration.xml")));
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Assert.assertNotNull(configurationManager);
        Exchange response = getProducerTemplate().request("jetty:http://localhost:" + getAvailablePort() + "/mock/cmd", exchange -> {
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
            exchange.getIn().setHeader("config", "current");
        });
        String actualConfig = response.getOut().getBody(String.class);
        assertXMLEqual(expectedConfig,actualConfig);
    }

    @DataProvider(name = "datasetTestConfigurations")
    public Object[][] dataSetConfigs(){
        return new Object[][]{
                {"configCommand-test-configuration.xml"}
        };
    }

    @Test(dataProvider = "datasetTestConfigurations")
    public void testWithLoadConfigurations(String testConfigFile) throws Exception {
        LOGGER.info("About to send Config command, total routes: {}", getProducerTemplate().getCamelContext().getRoutes().size());
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
        String defaultConfig = configurations.toString();

        configurations.setConfigurations(loadTestConfiguration(testConfigFile));
        testConfigFile = new String(Files.readAllBytes(Paths.get(pathToRequestFile + File.separator + testConfigFile)));

        Exchange response = getProducerTemplate().request("jetty:http://localhost:" + getAvailablePort() + "/mock/cmd", exchange -> {
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
            exchange.getIn().setHeader("config", "current");
        });
        String loadedConfig = configurations.getConfigurations().toString();
        Assert.assertNotEquals(loadedConfig,defaultConfig);

        String actualConfig = response.getOut().getBody(String.class);
        assertXMLEqual(testConfigFile, actualConfig);
    }

    private List<Configuration> loadTestConfiguration(String inputFile) throws Exception {
        InputStream inputStream = new FileInputStream(CONFIG_LOCATION + File.separator + inputFile);
        Unmarshaller unmarshaller = JAXBContext.newInstance(Configurations.class).createUnmarshaller();
        Configurations configurations = (Configurations)unmarshaller.unmarshal(inputStream);
        LOGGER.info("Loaded Configurations = {}", configurations.getConfigurations().size());
        return configurations.getConfigurations();

    }
}