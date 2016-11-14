package net.technolords.micro.output;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.domain.ResponseContext;
import net.technolords.micro.domain.jaxb.resource.SimpleResource;

public class ResponseContextGenerator {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Path pathToDataFolder = null;

    /**
     * Custom constructor, which caches the path to the data folder. Note that this can be null.
     *
     * @param pathToDataFolder
     *  The path to the data folder.
     */
    public ResponseContextGenerator(Path pathToDataFolder) {
        this.pathToDataFolder = pathToDataFolder;
    }

    /**
     * Auxiliary method that reads the response data as well as updating the internal cache so
     * subsequent reads will will served from memory. It also implements the delay and updates
     * the ResponseContext with an erroneous status code if the error rate is triggered.
     *
     * @param resource
     *  The resource to read and cache.
     *
     * @return
     *  The data associated with the resource (i.e. response).
     *
     * @throws IOException
     *  When reading the resource fails.
     */
    public ResponseContext readResourceCacheOrFile(SimpleResource resource) throws IOException, InterruptedException {
        // Add delay (only when applicable)
        if (resource.getDelay() > 0) {
            LOGGER.debug("About to delay {} ms", resource.getDelay());
            Thread.sleep(resource.getDelay());
        }
        // Apply response
        ResponseContext responseContext = new ResponseContext();
        if (resource.getCachedData() == null) {
            if (this.pathToDataFolder == null) {
                resource.setCachedData(this.readFromPackagedFile(resource.getResource()));
            } else {
                resource.setCachedData(this.readFromReferencedPath(resource.getResource()));
            }
        }
        // Apply content type
        responseContext.setResponse(resource.getCachedData());
        if (resource.getContentType() != null) {
            responseContext.setContentType(resource.getContentType());
        } else {
            responseContext.setContentType(this.fallbackLogicForContentType(resource));
        }
        // Apply custom error (only when applicable)
        if (resource.getErrorRate() > 0) {
            if (resource.getErrorRate() >= this.generateRandom()) {
                responseContext.setErrorCode(resource.getErrorCode());
            }
        }
        return responseContext;
    }

    /**
     * Auxiliary method to determine the content type (as fall back logic). In this case, the file extension is used.
     * When it .xml it will be 'application/xml' and otherwise 'application/json'.
     *
     * @param resource
     *  The resource associated with the fall back logic.
     *
     * @return
     *  The content type.
     */
    private String fallbackLogicForContentType(SimpleResource resource) {
        if (resource.getResource().toLowerCase().endsWith(".xml")) {
            return ResponseContext.XML_CONTENT_TYPE;
        } else {
            return ResponseContext.DEFAULT_CONTENT_TYPE;
        }
    }

    /**
     * Auxiliary method to read data from a packaged file.
     *
     * @param resourceReference
     *  The reference of the resource, or, path of the file (in the jar).
     * @return
     *  The content of the resource.
     *
     * @throws IOException
     *  When reading the resource fails.
     */
    private String readFromPackagedFile(String resourceReference) throws IOException {
        InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceReference);
        LOGGER.debug("Path to file exists: {}", fileStream.available());
        return new BufferedReader(new InputStreamReader(fileStream)).lines().collect(Collectors.joining("\n"));
    }

    /**
     * Auxiliary method to read data from a file.
     *
     * @param resourceReference
     *  The reference of the resource, or, path of the file (on file system).
     * @return
     *  The content of the resource.
     *
     * @throws IOException
     *  When reading the resource fails.
     */
    private String readFromReferencedPath(String resourceReference) throws IOException {
        Path pathToResource = this.pathToDataFolder.resolve(resourceReference);
        LOGGER.debug("Path to file exists: {}", Files.exists(pathToResource));
        return Files.lines(pathToResource).collect(Collectors.joining("\n"));
    }

    /**
     * Auxiliary method to generate a random number. This number will be in the range of [1, 100].
     *
     * @return
     *  A random number.
     */
    protected int generateRandom() {
        return (int)(Math.random() * 100 + 1);
    }
}
