package net.technolords.micro.registry.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.technolords.micro.test.factory.ConfigurationsFactory;

public class MetadataHelperTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test (description = "Test creation of the meta data string")
    public void testAddMetadataEntries() {
        StringBuilder buffer = new StringBuilder();
        MetadataHelper.addMetadataEntries(buffer, ConfigurationsFactory.createConfigurations());
        String expected = "\"post\": \"/mock/post1, /mock/post2\",\"get\": \"/mock/get\"";
        Assert.assertEquals(buffer.toString(), expected);
    }
}