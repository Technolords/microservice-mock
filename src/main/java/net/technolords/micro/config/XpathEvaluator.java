package net.technolords.micro.config;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import net.technolords.micro.domain.jaxb.Configuration;

public class XpathEvaluator {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ConfigurationToNamespaceContext configurationToNamespaceContext = null;
    private XPathFactory xPathFactory = null;

    /**
     * Auxiliary method that evaluates the given xpath expression with the given message.
     *
     * NOTE: currently only 'boolean' xpath expressions are supported. Meaning, any node list
     * selection or otherwise will fail. TODO: result typing, to support different xpath queries.
     *
     * @param xpathExpression
     *  The xpath expression to evaluate.
     * @param xmlMessage
     *  The xml message associated with the xpath evaluation.
     * @param configuration
     *  The configuration associated with the namespaces.
     *
     * @return
     *  The result of the xpath expression, which means it is either a match or not.
     *
     * @throws XPathExpressionException
     *  When evaluation the xpath expression fails.
     * @throws IOException
     *  When reading the input source fails.
     */
    public boolean evaluateXpathExpression(String xpathExpression, String xmlMessage, Configuration configuration) throws XPathExpressionException, IOException {
        XPathExpression xPathExpression = this.obtainXpathExpression(xpathExpression, configuration);
        LOGGER.debug("Xpath expression compiled, and is ready to be used for evaluation...");
        StringReader stringReader = new StringReader(xmlMessage);
        InputSource inputSource = new InputSource(stringReader);
        LOGGER.debug("Xml input source created, size: {}...", xmlMessage.length());
        stringReader.reset();
        Boolean result = (Boolean) xPathExpression.evaluate(inputSource, XPathConstants.BOOLEAN);
        LOGGER.debug("... xpath evaluated: {}", result);
        return result;
    }

    /**
     * Auxiliary method that creates a xpath object for further usage. This is achieved by compiling the given
     * xpath expression as well as using any namespaces declared in the given configuration.
     *
     * @param xpathExpression
     *  A string representing the xpath expression.
     * @param configuration
     *  The configuration associated with the namespaces.
     *
     * @return
     *  The compiled xpath expression.
     *
     * @throws XPathExpressionException
     *  When the compilation fails.
     */
    private XPathExpression obtainXpathExpression(String xpathExpression, Configuration configuration) throws XPathExpressionException {
        LOGGER.debug("About to compile xpath expression...");
        if (this.xPathFactory == null) {
            this.xPathFactory = XPathFactory.newInstance();
        }
        XPath xPath = this.xPathFactory.newXPath();
        if (this.configurationToNamespaceContext == null) {
            this.configurationToNamespaceContext = new ConfigurationToNamespaceContext();
        }
        xPath.setNamespaceContext(this.configurationToNamespaceContext.createNamespaceContext(configuration));
        return xPath.compile(xpathExpression);
    }
}
