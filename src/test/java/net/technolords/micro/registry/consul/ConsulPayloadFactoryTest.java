package net.technolords.micro.registry.consul;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.technolords.micro.test.PathSupport;
import net.technolords.micro.test.factory.ConfigurationsFactory;
import net.technolords.micro.test.factory.ServiceFactory;
import net.technolords.micro.util.WhitespaceFilter;

public class ConsulPayloadFactoryTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testPayloadGenerationForRegistration() throws IOException {
        Path resources = PathSupport.getPathToTestResources();
        Path jsonFile = Paths.get(resources.toString(), "/json/consul-payload-register.json");
        LOGGER.debug("Json file exists: {}", Files.exists(jsonFile));
        String expected = new String(Files.readAllBytes(jsonFile));
        String actual = ConsulPayloadFactory.generatePayloadForRegister(ServiceFactory.createService(), ConfigurationsFactory.createConfigurations());
        Assert.assertEquals(WhitespaceFilter.filter(actual), WhitespaceFilter.filter(expected));
    }

}