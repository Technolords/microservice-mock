package net.technolords.micro.test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is designed to support retrieval of files from various locations:
 *
 * - main resources: these resources will be embedded in the final Jar file.
 * - test resources: these resources are used for tests only.
 *
 * In other words:
 *
 * src/main/resources
 * src/test/resources
 * src/test/resources/config/log
 * src/test/resources/config/mock
 * src/test/resources/data/request
 * src/test/resources/data/response
 *
 * The idea is to be platform independent.
 */
public class PathSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(PathSupport.class);
    private static final String SRC = "src";
    private static final String MAIN = "main";
    private static final String TEST = "test";
    private static final String RESOURCES = "resources";
    private static final String CONFIG = "config";
    private static final String LOG = "log";
    private static final String MOCK = "mock";
    private static final String DATA = "data";
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";

    // src/main/resources
    public static Path getPathToMainResources() {
        Path pathToData = FileSystems.getDefault().getPath(getMainResourcesAsString());
        LOGGER.debug("Path to main resources: {}", pathToData.toAbsolutePath().toString());
        return pathToData;
    }

    // src/test/resources
    public static Path getPathToTestResources() {
        Path pathToData = FileSystems.getDefault().getPath(getTestResourcesAsString());
        LOGGER.debug("Path to test resources: {}", pathToData.toAbsolutePath().toString());
        return pathToData;
    }

    // src/test/resources/config/log
    public static Path getPathToTestConfigForLogResources() {
        Path pathToData = FileSystems.getDefault().getPath(getTestConfigResourcesForLogAsString());
        LOGGER.debug("Path to test configuration for log resources: {}", pathToData.toAbsolutePath().toString());
        return pathToData;
    }

    // src/test/resources/config/mock
    public static Path getPathToTestConfigForMockResources() {
        Path pathToData = FileSystems.getDefault().getPath(getTestConfigResourcesForMockAsString());
        LOGGER.debug("Path to test configuration for mock resources: {}", pathToData.toAbsolutePath().toString());
        return pathToData;
    }

    // src/test/resources/data/request
    public static Path getPathToTestDataForRequestResources() {
        Path pathToData = FileSystems.getDefault().getPath(getTestDataResourcesForRequest());
        LOGGER.debug("Path to test data for request resources: {}", pathToData.toAbsolutePath().toString());
        return pathToData;
    }

    // src/test/resources/data/response
    public static Path getPathToTestDataForResponseResources() {
        Path pathToData = FileSystems.getDefault().getPath(getTestDataResourcesForResponse());
        LOGGER.debug("Path to test data for response resources: {}", pathToData.toAbsolutePath().toString());
        return pathToData;
    }

    // ---------------
    // Private methods
    // ---------------

    private static String getResourcesAsString(String type) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(SRC).append(File.separator);
        buffer.append(type).append(File.separator);
        buffer.append(RESOURCES);
        return buffer.toString();
    }

    // src/main/resources
    private static String getMainResourcesAsString() {
        return getResourcesAsString(MAIN);
    }

    // src/test/resources
    private static String getTestResourcesAsString() {
        return getResourcesAsString(TEST);
    }

    // src/test/resources/config
    private static String getTestConfigResourcesAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getTestResourcesAsString()).append(File.separator);
        buffer.append(CONFIG);
        return buffer.toString();
    }

    // src/test/resources/config/log
    private static String getTestConfigResourcesForLogAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getTestConfigResourcesAsString()).append(File.separator);
        buffer.append(LOG);
        return buffer.toString();
    }

    // src/test/resources/config/mock
    private static String getTestConfigResourcesForMockAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getTestConfigResourcesAsString()).append(File.separator);
        buffer.append(MOCK);
        return buffer.toString();
    }

    // src/test/resources/data
    private static String getTestDataResourcesAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getTestResourcesAsString()).append(File.separator);
        buffer.append(DATA);
        return buffer.toString();
    }

    // src/test/resources/data/request
    private static String getTestDataResourcesForRequest() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getTestDataResourcesAsString()).append(File.separator);
        buffer.append(REQUEST);
        return buffer.toString();
    }

    // src/test/resources/data/response
    private static String getTestDataResourcesForResponse() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getTestDataResourcesAsString()).append(File.separator);
        buffer.append(RESPONSE);
        return buffer.toString();
    }
}
