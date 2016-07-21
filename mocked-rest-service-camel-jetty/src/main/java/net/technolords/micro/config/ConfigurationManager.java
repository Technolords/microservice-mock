package net.technolords.micro.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import net.technolords.micro.config.jaxb.Configuration;
import net.technolords.micro.config.jaxb.Configurations;
import net.technolords.micro.config.jaxb.resource.Complex;
import net.technolords.micro.config.jaxb.resource.Group;
import net.technolords.micro.config.jaxb.resource.Simple;

/**
 * Created by Technolords on 2016-Jul-20.
 */
public class ConfigurationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String HTTP_POST = "POST";
    private static final String PATH_TO_CONFIG_FILE = "config/configuration.xml";
    private Configurations configurations = null;
    private Map<String, Configuration> getConfigurations = new HashMap<>();
    private Map<String, Configuration> postConfigurations = new HashMap<>();

    public void initializeConfig() throws JAXBException {
        if (this.configurations == null) {
            LOGGER.info("About to initialize resources from configuration file...");
            Unmarshaller unmarshaller = JAXBContext.newInstance(Configurations.class).createUnmarshaller();
            this.configurations = (Configurations) unmarshaller.unmarshal(this.getClass().getClassLoader().getResourceAsStream(PATH_TO_CONFIG_FILE));
            LOGGER.info("Total loaded resources: {}", this.configurations.getConfigurations().size());
        }
        for (Configuration configuration : this.configurations.getConfigurations()) {
            if (HTTP_POST.equals(configuration.getType().toUpperCase())) {
                // Add resource to post configuration group
                this.postConfigurations.put(configuration.getUrl(), configuration);
            } else {
                // Add resource to get configuration group
                this.getConfigurations.put(configuration.getUrl(), configuration);
            }
        }
        LOGGER.info("URL mappings completed [{} for post, {} for get]", this.postConfigurations.size(), this.getConfigurations.size());
    }

    public String findResponseForGetOperationWithPath(String path) throws JAXBException {
        this.initializeConfig();
        LOGGER.debug("About to find response for get operation with path: {}", path);
        if (this.getConfigurations.containsKey(path)) {
            LOGGER.debug("... found, proceeding to the data part...");
            Configuration configuration = this.getConfigurations.get(path);
            Simple resource = configuration.getGetResource();
            if (resource.getCachedData() != null) {
                return resource.getCachedData();
            } else {
                // Load and update cache
                LOGGER.info("About to load data from: {}", resource.getResource());
                try {
                    return this.readResourceAndUpdateCacheData(resource);
                } catch (IOException e) {
                    LOGGER.error("Unable to load resource", e);
                }
            }
        }
        LOGGER.debug("... not found!");
        return null;
    }

    public String findResponseForPostOperationWithPathAndMessage(String path, String message) throws IOException, JAXBException, XPathExpressionException {
        this.initializeConfig();
        LOGGER.info("About to find response for post operation with path: {}", path);
        if (this.postConfigurations.containsKey(path)) {
            LOGGER.info("... found, proceeding to the data part...");
            Configuration configuration = this.postConfigurations.get(path);
            // Iterate of the resource, and verify whether the xpath matches with the data
            Group group = configuration.getPostResources();
            for (Complex complex : group.getResources()) {
                if (complex.getXpath() != null) {
                    LOGGER.info("... found xpath: {}", complex.getXpath().getXpath());
                    if (this.xpathMatchWithMessage(complex.getXpath().getXpath(), message)) {
                        LOGGER.info("... xpath matched, about to find associated resource");
                        Simple resource = complex.getResource();
                        if (resource != null) {
                            LOGGER.info("... resource available, about to load the data from: {}", resource.getResource());
                            return this.readResourceAndUpdateCacheData(resource);
                        }
                    }
                } else {
                    Simple resource = complex.getResource();
                    if (resource != null) {
                        // No xpath defined, means automatic match!
                        LOGGER.info("No xpath configured, about to load the data from: {}", resource.getResource());
                        return this.readResourceAndUpdateCacheData(resource);
                    }
                }
            }
        }
        LOGGER.info("... not found!");
        return null;
    }

    private String readResourceAndUpdateCacheData(Simple resource) throws IOException {
        String data = this.readResource(resource.getResource());
        resource.setCachedData(data);
        return resource.getCachedData();
    }

    private String readResource(String path) throws IOException {
        InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        LOGGER.debug("Path to file exists: {}",  fileStream.available());
        return new BufferedReader(new InputStreamReader(fileStream)).lines().collect(Collectors.joining("\n"));
    }

    private boolean xpathMatchWithMessage(String xpathExpression, String xmlMessage) throws XPathExpressionException {
        XPathExpression xPathExpression = this.obtainXpathExpression(xpathExpression);
        LOGGER.debug("Xpath expression created...");
        StringReader stringReader = new StringReader(xmlMessage);
        InputSource inputSource = new InputSource(stringReader);
        LOGGER.debug("Xml input source created...");
        String result = xPathExpression.evaluate(inputSource);
        LOGGER.info("... xpath evaluated: {}", result);
//        if (result != null) {
//            return true;
//        }
        return false;
    }

    private XPathExpression obtainXpathExpression(String xpathExpression) throws XPathExpressionException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        return xPath.compile(xpathExpression);
    }

}
