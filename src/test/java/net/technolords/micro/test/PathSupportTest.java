package net.technolords.micro.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PathSupportTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // src/main/resources
    @Test (description = "Validate path to main resources")
    public void testPathToMainResources() {
        LOGGER.debug("About to validate path to main resources...");
        Path path = PathSupport.getPathToMainResources();
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path));
    }

    // src/test/resources
    @Test (description = "Validate path to test resources")
    public void testPathToTestResources() {
        LOGGER.debug("About to validate path to test resources...");
        Path path = PathSupport.getPathToTestResources();
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path));
    }

    // src/test/resources/config/log
    @Test (description = "Validate path to test configuration for log resources")
    public void testPathToTestConfigurationForLogResources() {
        LOGGER.debug("About to validate path to test configuration for log resources");
        Path path = PathSupport.getPathToTestConfigForLogResources();
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path));
    }

    // src/test/resources/config/mock
    @Test (description = "Validate path to test configuration for mock resources")
    public void testPathToTestConfigurationForMockResources() {
        LOGGER.debug("About to validate path to test configuration for mock resources");
        Path path = PathSupport.getPathToTestConfigForMockResources();
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path));
    }

    // src/test/resources/data/request
    @Test (description = "Validate path to test data for request resources")
    public void testPathToTestDataForRequestResources() {
        LOGGER.debug("About to validate path to test data for request resources");
        Path path = PathSupport.getPathToTestDataForRequestResources();
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path));
    }

    // src/test/resources/data/response
    @Test (description = "Validate path to test data for response resources")
    public void testPathToTestDataForResponseResources() {
        LOGGER.debug("About to validate path to test data for request resources");
        Path path = PathSupport.getPathToTestDataForResponseResources();
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path));
    }
}