package net.technolords.micro.config;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

import net.technolords.micro.registry.MockRegistry;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.technolords.micro.input.ConfigurationSelector;
import net.technolords.micro.input.json.JsonPathEvaluator;
import net.technolords.micro.input.xml.XpathEvaluator;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.model.jaxb.query.QueryGroup;
import net.technolords.micro.model.jaxb.query.QueryGroups;
import net.technolords.micro.model.jaxb.query.QueryParameter;
import net.technolords.micro.model.jaxb.resource.ResourceGroup;
import net.technolords.micro.model.jaxb.resource.ResourceGroups;
import net.technolords.micro.model.jaxb.resource.SimpleResource;
import net.technolords.micro.output.ResponseContextGenerator;

public class ConfigurationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_PATCH = "PATCH";
    public static final String HTTP_DELETE = "DELETE";
    private static final String PATH_TO_CONFIG_FILE = "xml/default-configuration.xml";
    private static final String PATH_TO_SCHEMA_FILE = "xsd/configurations.xsd";
    private final ResponseContextGenerator responseContextGenerator;
    private final ConfigurationSelector configurationSelector = new ConfigurationSelector();
    private Configurations configurations;
    private XpathEvaluator xpathEvaluator;
    private JsonPathEvaluator jsonPathEvaluator;
    private final Map<String, Map<String, Configuration>> allConfigurations = new HashMap<>();

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

    public void reloadConfiguration() throws IOException, SAXException, JAXBException {
        LOGGER.info("About to reload configuration...");
        String pathToConfig = MockRegistry.findConfiguredConfig();
        LOGGER.info("Using configuration file: {}", pathToConfig);
        Path path = FileSystems.getDefault().getPath(pathToConfig);
        LOGGER.info("File exist: {}", Files.exists(path));
        // Validation
        InputStream inputStreamForValidation = Files.newInputStream(path, StandardOpenOption.READ);
        this.validateConfigurationFile(inputStreamForValidation);
        // Initialize configuration
        InputStream inputStreamForConfig = Files.newInputStream(path, StandardOpenOption.READ);
        this.initializeConfiguration(inputStreamForConfig);
    }

    public ResponseContext findResponseForGetOperation(String path, String parameters) throws IOException, InterruptedException {
        Map<String, Configuration> getConfigurations = this.allConfigurations.get(HTTP_GET);
        LOGGER.debug("Total configurations for GET: {}", getConfigurations.size());
        return this.findResponseForParameterBasedOperation(path, parameters, getConfigurations);
    }

    public ResponseContext findResponseForDeleteOperation(String path, String parameters) throws IOException, InterruptedException {
        Map<String, Configuration> deleteConfigurations = this.allConfigurations.get(HTTP_DELETE);
        LOGGER.debug("Total configurations for DELETE: {}", deleteConfigurations.size());
        return this.findResponseForParameterBasedOperation(path, parameters, deleteConfigurations);
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
    public ResponseContext findResponseForParameterBasedOperation(String path, String parameters, Map<String, Configuration> configurations) throws IOException, InterruptedException {
        LOGGER.debug("About to find response for parameter based operation with path: {}", path);
        Configuration configuration = this.configurationSelector.findMatchingConfiguration(path, configurations);
        if (configuration != null) {
            LOGGER.debug("... found, proceeding to the data part...");
            SimpleResource resource = null;
            // Check for query groups
            if (configuration.getQueryGroups() != null && (parameters != null && !parameters.isEmpty())) {
                resource = this.findMatchForQueryGroup(configuration.getQueryGroups(), parameters);
            }
            // Fall back on default when no group match is found
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

    protected SimpleResource findMatchForQueryGroup(QueryGroups queryGroups, String parameters) {
        if (queryGroups == null) {
            return null;
        }
        // check for query group
        for (QueryGroup queryGroup : queryGroups.getQueryGroups()) {
            SimpleResource resource = this.findMatchForQueryParameters(queryGroup, this.extractQueryParametersFromString(parameters));
            if (resource != null) {
                return resource;
            }
        }
        // If match found, stop checking rest
        return null;
    }

    protected SimpleResource findMatchForQueryParameters(QueryGroup queryGroup, Map<String, String> parameters) {
        for (QueryParameter queryParameter : queryGroup.getQueryParameters()) {
            // TODO: support placeholders to reduce configuration file
            // Satisfy key being present
            String key = queryParameter.getKey();
            if (parameters.containsKey(key)) {
                // Satisfy the value
                String configuredValue = queryParameter.getValue();
                String receivedValue = parameters.get(key);
                if (!configuredValue.equals(receivedValue)) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return queryGroup.getSimpleResource();
    }

    // key1=11&key=12
    protected Map<String, String> extractQueryParametersFromString(String parameters) {
        Map<String, String> result = new HashMap<>();
        if (parameters.isEmpty()) {
            return result;
        }
        Pattern keyValuePattern = Pattern.compile("&");
        result = keyValuePattern
                .splitAsStream(parameters)
                .map(keyValue -> keyValue.split("="))
                .filter(split -> split.length % 2 == 0)
                .collect(toMap(split -> split[0], split -> split[1]));
        return result;
    }

    public ResponseContext findResponseForPostOperation(String path, String body, String discriminator) throws XPathExpressionException, IOException, InterruptedException {
        LOGGER.debug("About to find response for post operation with path: {} and discriminator: {}", path, discriminator);
        Map<String, Configuration> postConfigurations = this.allConfigurations.get(HTTP_POST);
        LOGGER.debug("Total configurations for POST: {}", postConfigurations.size());
        return this.findResponseForBodyBasedOperation(path, body, discriminator, postConfigurations);
    }

    public ResponseContext findResponseForPutOperation(String path, String body, String discriminator) throws XPathExpressionException, IOException, InterruptedException {
        LOGGER.debug("About to find response for put operation with path: {} and discriminator: {}", path, discriminator);
        Map<String, Configuration> putConfigurations = this.allConfigurations.get(HTTP_PUT);
        LOGGER.debug("Total configurations for PUT: {}", putConfigurations.size());
        return this.findResponseForBodyBasedOperation(path, body, discriminator, putConfigurations);
    }

    public ResponseContext findResponseForPatchOperation(String path, String body, String discriminator) throws XPathExpressionException, IOException, InterruptedException {
        LOGGER.debug("About to find response for patch operation with path: {} and discriminator: {}", path, discriminator);
        Map<String, Configuration> putConfigurations = this.allConfigurations.get(HTTP_PATCH);
        LOGGER.debug("Total configurations for PATCH: {}", putConfigurations.size());
        return this.findResponseForBodyBasedOperation(path, body, discriminator, putConfigurations);
    }

    /**
     * Auxiliary method to find a response for a given request, based on the path and the body.
     *
     * @param path
     *  The path associated with the request.
     * @param body
     *  The body associated with the request.
     * @param discriminator
     *  The discriminator associated with the request, used to determine whether a xpath or jsonpath expression
     *  has to be evaluated.
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
    protected ResponseContext findResponseForBodyBasedOperation(String path, String body, String discriminator, Map<String, Configuration> configurations) throws IOException, XPathExpressionException, InterruptedException {
        LOGGER.debug("About to find response for body based operation with path: {}", path);
        if (Strings.isBlank(discriminator)) {
            discriminator = "default";
        } else {
            discriminator = discriminator.toLowerCase();
        }
        LOGGER.trace("Discriminator (content-type): {}", discriminator);
        if (LOGGER.isDebugEnabled()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("... found path mappings:\n");
            configurations.forEach((s, configuration) -> {
                buffer.append("\t").append(s).append("\n");
            });
            LOGGER.debug(buffer.toString());
        }
        Configuration configuration = this.configurationSelector.findMatchingConfiguration(path, configurations);
        if (configuration != null) {
            LOGGER.debug("... found, proceeding to the data part...");
            // Iterate the resources, and verify whether the xpath matches with the data
            ResourceGroups resourceGroups = configuration.getResourceGroups();
            if (resourceGroups != null) {
                LOGGER.debug("Total resource groups configured: {}", resourceGroups.getResourceGroup().size());
                for (ResourceGroup resourceGroup : resourceGroups.getResourceGroup()) {
                    switch (discriminator) {
                        case "json":
                        case "application/json":
                            LOGGER.trace("Checking for jsonpath...");
                            if (resourceGroup.getJsonpathConfig() != null) {
                                LOGGER.debug("... found jsonpath: {}", resourceGroup.getJsonpathConfig().getJsonpath().trim());
                                if (this.jsonPathEvaluator.evaluateXpathExpression(resourceGroup.getJsonpathConfig().getJsonpath().trim(), body, configuration)) {
                                    LOGGER.debug("... jsonpath matched, about to find associated resource");
                                    return this.responseContextGenerator.readResourceCacheOrFile(resourceGroup.getSimpleResource());
                                }
                                // Note: if there is no match, continue with the resource group loop
                                break;
                            } else {
                                LOGGER.debug("No jsonpath configured, about to load the data from: {}", resourceGroup.getSimpleResource().getResource());
                                return this.responseContextGenerator.readResourceCacheOrFile(resourceGroup.getSimpleResource());
                            }
                        case "xml":
                        case "application/xml":
                        default:
                            LOGGER.trace("Checking for xpath...");
                            // No type detected, falling back to default (which is xml)
                            if (resourceGroup.getXpathConfig() != null) {
                                LOGGER.debug("... found xpath: {}", resourceGroup.getXpathConfig().getXpath());
                                if (this.xpathEvaluator.evaluateXpathExpression(resourceGroup.getXpathConfig().getXpath(), body, configuration)) {
                                    LOGGER.debug("... xpath matched, about to find associated resource");
                                    return this.responseContextGenerator.readResourceCacheOrFile(resourceGroup.getSimpleResource());
                                }
                                // Note: if there is no match, continue with the resource group loop
                                break;
                            } else {
                                LOGGER.debug("No xpath configured, about to load the data from: {}", resourceGroup.getSimpleResource().getResource());
                                return this.responseContextGenerator.readResourceCacheOrFile(resourceGroup.getSimpleResource());
                            }
                    }
                }
            } else {
                LOGGER.debug("No resource groups, defaulting to simple resource...");
                return this.responseContextGenerator.readResourceCacheOrFile(configuration.getSimpleResource());
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
            String type = configuration.getType();
            Map<String, Configuration> foundConfigurationMap = this.allConfigurations.get(type);
            if (foundConfigurationMap == null) {
                foundConfigurationMap = new HashMap<>();
            }
            foundConfigurationMap.put(configuration.getUrl(), configuration);
            this.allConfigurations.put(type, foundConfigurationMap);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("... done, found URL mappings:\n");
        this.allConfigurations.forEach((type, typeConfigurationMap) -> buffer.append("\ttype: ").append(String.format("%-20s", type)).append("total: ").append(typeConfigurationMap.size()).append("\n"));
        LOGGER.info(buffer.toString());
        this.xpathEvaluator = new XpathEvaluator();
        this.jsonPathEvaluator = new JsonPathEvaluator();
    }

}
