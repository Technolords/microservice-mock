package net.technolords.micro.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.model.jaxb.query.QueryGroup;
import net.technolords.micro.model.jaxb.query.QueryGroups;
import net.technolords.micro.model.jaxb.resource.ResourceGroup;
import net.technolords.micro.model.jaxb.resource.ResourceGroups;
import net.technolords.micro.model.jaxb.resource.SimpleResource;
import net.technolords.micro.input.ConfigurationSelector;
import net.technolords.micro.input.xml.XpathEvaluator;
import net.technolords.micro.output.ResponseContextGenerator;

public class ConfigurationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";
    private static final String PATH_TO_CONFIG_FILE = "xml/default-configuration.xml";
    private static final String PATH_TO_SCHEMA_FILE = "xsd/configurations.xsd";
    private ResponseContextGenerator responseContextGenerator;
    private ConfigurationSelector configurationSelector = new ConfigurationSelector();
    private Configurations configurations = null;
    private XpathEvaluator xpathEvaluator = null;
    private Map<String, Configuration> getConfigurations = new HashMap<>();
    private Map<String, Configuration> postConfigurations = new HashMap<>();

    /**
     * Custom constructor that initializes the Configuration class, but only when it is compliant with the XSD.
     *
     * @param pathToConfig
     *  The path to the configuration file (null value means it will fall back on embedded file, i.e. jar file).
     * @param pathToData
     *  The path to the data folder (null value means it will fall back on the embedded files, i.e. jar file).
     *
     * @throws JAXBException
     *  When creating the Configuration fails.
     * @throws IOException
     *  When reading the configuration file fails.
     * @throws SAXException
     *  When validating the configuration file fails.
     */
    public ConfigurationManager(String pathToConfig, String pathToData) throws JAXBException, IOException, SAXException {
        InputStream inputStreamForValidation, inputStreamForConfig; // Streams can be read only once
        if (pathToConfig == null || pathToConfig.isEmpty()) {
            pathToConfig = PATH_TO_CONFIG_FILE;
            // Set input stream to a resource inside this jar file
            inputStreamForValidation = this.getClass().getClassLoader().getResourceAsStream(pathToConfig);
            inputStreamForConfig = this.getClass().getClassLoader().getResourceAsStream(pathToConfig);
        } else {
            LOGGER.info("Using configuration file: {}", pathToConfig);
            Path path = FileSystems.getDefault().getPath(pathToConfig);
            LOGGER.info("File exist: {}", Files.exists(path));
            // Set input stream to a resource located on file system (read only)
            inputStreamForValidation = Files.newInputStream(path, StandardOpenOption.READ);
            inputStreamForConfig = Files.newInputStream(path, StandardOpenOption.READ);
        }
        if (pathToData != null) {
            LOGGER.info("Using data folder: {}", pathToData);
            Path pathToDataFolder = FileSystems.getDefault().getPath(pathToData);
            LOGGER.info("Folder exist: {}, and is folder: {}", Files.exists(pathToDataFolder), Files.isDirectory(pathToDataFolder));
            this.responseContextGenerator = new ResponseContextGenerator(pathToDataFolder);
        } else {
            this.responseContextGenerator = new ResponseContextGenerator(null);
        }
        // Validate configuration file
        this.validateConfigurationFile(inputStreamForValidation);
        // Initialize configuration
        this.initializeConfiguration(inputStreamForConfig);
    }

    /**
     * Auxiliary method to find a response for a given GET request, based on the path.
     *
     * @param path
     *  The path associated with the get request.
     * @return
     *  The response associated with the get request.
     *
     * @throws IOException
     *  When reading the response fails.
     * @throws InterruptedException
     *  When delaying the response fails.
     */
    public ResponseContext findResponseForGetOperationWithPath(String path) throws IOException, InterruptedException {
        LOGGER.debug("About to find response for get operation with path: {}", path);
        Configuration configuration = this.configurationSelector.findMatchingConfiguration(path, this.getConfigurations);
        if (configuration != null) {
            LOGGER.debug("... found, proceeding to the data part...");
            SimpleResource resource = null;
            // Check for query groups
            if (configuration.getQueryGroups().getQueryGroups().size() > 0) {
                resource = this.findMatchForQueryGroup(configuration.getQueryGroups());
            }
            // If match found, stop checking rest
            if (resource == null) {
                resource = configuration.getSimpleResource();
            }
            // Load and update cache
            LOGGER.debug("About to load data from: {}", resource.getResource());
            return this.responseContextGenerator.readResourceCacheOrFile(resource);
        }
        LOGGER.debug("... not found!");
        return null;
    }

    private SimpleResource findMatchForQueryGroup(QueryGroups queryGroups) {
        SimpleResource resource = null;
        // check for query group
        for (QueryGroup queryGroup : queryGroups.getQueryGroups()) {

        }
        // If match found, stop checking rest
        return resource;
    }

    /**
     * Auxiliary method to find a response for a given POST request, based on the path and the message (body).
     *
     * @param path
     *  The path associated with the post request.
     * @param message
     *  The message associated with the post request.
     *
     * @return
     *  The response associated with the post request.
     *
     * @throws IOException
     *  When reading the response fails.
     * @throws XPathExpressionException
     *  When evaluation the xpath expression fails.
     * @throws InterruptedException
     *  When delaying the response fails.
     */
    public ResponseContext findResponseForPostOperationWithPathAndMessage(String path, String message) throws IOException, XPathExpressionException, InterruptedException {
        LOGGER.debug("About to find response for post operation with path: {}", path);
        if (this.postConfigurations.containsKey(path)) {
            LOGGER.debug("... found, proceeding to the data part...");
            Configuration configuration = this.configurationSelector.findMatchingConfiguration(path, this.postConfigurations);
            // Iterate the resources, and verify whether the xpath matches with the data
            ResourceGroups resourceGroups = configuration.getResourceGroups();
            for (ResourceGroup resourceGroup : resourceGroups.getResourceGroup()) {
                if (resourceGroup.getXpathConfig() != null) {
                    LOGGER.debug("... found xpath: {}", resourceGroup.getXpathConfig().getXpath());
                    if (this.xpathEvaluator.evaluateXpathExpression(resourceGroup.getXpathConfig().getXpath(), message, configuration)) {
                        LOGGER.debug("... xpath matched, about to find associated resource");
                        return this.responseContextGenerator.readResourceCacheOrFile(resourceGroup.getSimpleResource());
                    }
                } else {
                    LOGGER.debug("No xpath configured, about to load the data from: {}", resourceGroup.getSimpleResource().getResource());
                    return this.responseContextGenerator.readResourceCacheOrFile(resourceGroup.getSimpleResource());
                }
            }
        }
        LOGGER.debug("... not found!");
        return null;
    }

    public Configurations getConfigurations() {
        return this.configurations;
    }

    /**
     * Auxiliary method to validate the configuration file.
     *
     * @param inputStream
     *  An input stream of the configuration file.
     *
     * @throws IOException
     *  When reading the configuration file fails.
     * @throws SAXException
     *  When the configuration file is not valid.
     */
    private void validateConfigurationFile(InputStream inputStream) throws IOException, SAXException {
        LOGGER.info("About to validate the configuration...");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source xsdSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(PATH_TO_SCHEMA_FILE));
        Schema schema = schemaFactory.newSchema(xsdSource);
        Validator validator = schema.newValidator();
        Source sourceToConfig = new StreamSource(inputStream);
        validator.validate(sourceToConfig);
        LOGGER.info("... valid, proceeding...");
    }

    /**
     * Auxiliary method to initialize the configuration manager, which means:
     * - reading and parsing the xml configuration
     * - instantiating a xpath evaluator
     *
     * @param inputStream
     *  An input stream of the configuration file.
     *
     * @throws JAXBException
     *  When parsing the XML configuration file fails.
     */
    protected void initializeConfiguration(InputStream inputStream) throws JAXBException {
        LOGGER.info("About to initialize the configuration...");
        Unmarshaller unmarshaller = JAXBContext.newInstance(Configurations.class).createUnmarshaller();
        this.configurations = (Configurations) unmarshaller.unmarshal(inputStream);
        LOGGER.debug("Total loaded resources: {}", this.configurations.getConfigurations().size());
        for (Configuration configuration : this.configurations.getConfigurations()) {
            if (HTTP_POST.equals(configuration.getType().toUpperCase())) {
                // Add resource to post configuration group
                this.postConfigurations.put(configuration.getUrl(), configuration);
            } else {
                // Add resource to get configuration group
                this.getConfigurations.put(configuration.getUrl(), configuration);
            }
        }
        LOGGER.info("... done, URL mappings parsed [{} for POST, {} for GET]", this.postConfigurations.size(), this.getConfigurations.size());
        this.xpathEvaluator = new XpathEvaluator();
    }

}
