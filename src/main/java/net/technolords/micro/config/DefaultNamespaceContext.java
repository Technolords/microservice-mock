package net.technolords.micro.config;

import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultNamespaceContext implements NamespaceContext {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Map<String, String> namespaceConfig;

    public DefaultNamespaceContext(Map<String, String> myNamespaceConfig) {
        this.namespaceConfig = myNamespaceConfig;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        LOGGER.debug("getNamespaceURI called with: {}", prefix);
        if (this.namespaceConfig.containsKey(prefix)) {
            return this.namespaceConfig.get(prefix);
        }
        return XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        LOGGER.debug("getPrefix called with: {}", namespaceURI);
        for (String key : this.namespaceConfig.keySet()) {
            if (this.namespaceConfig.get(key).equals(namespaceURI)) {
                return key;
            }
        }
        return XMLConstants.DEFAULT_NS_PREFIX;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        LOGGER.debug("getPrefixes called with: {}", namespaceURI);
        return this.namespaceConfig.keySet().iterator();
    }
}
