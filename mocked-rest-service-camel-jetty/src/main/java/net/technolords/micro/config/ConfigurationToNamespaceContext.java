package net.technolords.micro.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.jaxb.Configuration;
import net.technolords.micro.config.jaxb.namespace.NamespaceConfig;
import net.technolords.micro.config.jaxb.namespace.NamespaceList;

/**
 * Created by Technolords on 2016-Jul-25.
 */
public class ConfigurationToNamespaceContext {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Auxiliary method to convert a Configuration into a NamespaceContext
     *
     * @param configuration
     *  A reference to a configuration object.
     *
     * @return
     *  A reference to a NamespaceContext.
     */
    public NamespaceContext createNamespaceContext(Configuration configuration) {
        if (configuration != null) {
            // Namespaces are only relevant for Post messages
            if (ConfigurationManager.HTTP_POST.equals(configuration.getType())) {
                return new DefaultNamespaceContext(this.createNamespaceMapping(configuration));
            }
        }
        return null;
    }

    /**
     * Auxiliary method to create a name space mapping, which holds a prefix and an associated URI, to
     * support a look up mechanism (implemented by the NamespaceContext interface). For example:
     *
     * "traxis", "urn:eventis:traxisweb:1.0"
     *
     * @param configuration
     *  The configuration associated with the name spaces.
     *
     * @return
     *  The name space mapping.
     */
    private Map<String, String> createNamespaceMapping(Configuration configuration) {
        Map<String, String> result = new HashMap<>();
        if (configuration != null) {
            // Namespaces are only relevant for Post messages
            if (ConfigurationManager.HTTP_POST.equals(configuration.getType())) {
                if (configuration.getNamespaceList() != null) {
                    NamespaceList namespaceList = configuration.getNamespaceList();
                    for (NamespaceConfig namespaceConfig : namespaceList.getNamespaces()) {
                        result.put(namespaceConfig.getPrefix(), namespaceConfig.getNamespaceURI());
                        LOGGER.info("Added namespace with prefix: {} to collection, now size {}", namespaceConfig.getPrefix(), result.size());
                    }
                }
            }
        }
        return result;
    }
}
