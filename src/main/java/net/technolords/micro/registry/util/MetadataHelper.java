package net.technolords.micro.registry.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.technolords.micro.model.jaxb.Configuration;

public class MetadataHelper {

    /**
     * Auxiliary method to add meta data entries. This method is typically invoked from the PayloadFactories
     * i.e. ConsulPayloadFactory and the EurekaPayloadFactory.
     *
     * Depending on the configuration, the entries are added by http type (get, post, ...) followed
     * with a comma separated list of matchers (rest paths). In other words, the buffer is extended with
     * something like:
     *
     *  "get": "/mock/get",
     *  "post": "/mock/post1, /mock/post2"
     *
     * @param buffer
     *  The buffer associated with the added entries.
     * @param configurations
     *  The configurations associated with the entries.
     */
    public static void addMetadataEntries(StringBuilder buffer, List<Configuration> configurations) {
        Map<String, List<String>> servicesByType = getServicesByType(configurations);
        int numberOfTypes = servicesByType.size();
        int currentNumber = 1;
        for (String type: servicesByType.keySet()) {
            // Add entries such as: "post": "/mock/post1, /mock/post2"
            buffer.append("\"").append(type).append("\": \"");
            List<String> servicesByURL = servicesByType.get(type);
            int numberOfUrls = servicesByURL.size();
            int currentURL = 1;
            for(String url: servicesByURL) {
                buffer.append(url);
                buffer.append(currentURL < numberOfUrls ? ", " : "");
                currentURL++;
            }
            buffer.append(currentNumber < numberOfTypes ? "\"," : "\"");
            currentNumber++;
        }
    }

    /**
     * Auxiliary method to 'convert' a list of configurations to a map. This data structure is more
     * convenient to generate the meta data tags.
     *
     * @param configurations
     *  The configurations associated with the map.
     *
     * @return
     *  A map of services by type.
     */
    public static Map<String, List<String>> getServicesByType(List<Configuration> configurations) {
        Map<String, List<String>> servicesByType = new HashMap<>();
        for (Configuration configuration : configurations) {
            String type = configuration.getType();
            if (!servicesByType.containsKey(type)) {
                List<String> services = new ArrayList<>();
                services.add(configuration.getUrl());
                servicesByType.put(configuration.getType(), services);
            } else {
                List<String> services = servicesByType.get(type);
                services.add(configuration.getUrl());
            }
        }
        return servicesByType;
    }
}
