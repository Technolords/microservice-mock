package net.technolords.micro.registry.consul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.HealthCheck;
import net.technolords.micro.model.jaxb.registration.Service;

public class ConsulPayloadFactory {

    /**
     * Create a payload like:
     *
     * {
     *  "ID": "mock-1",
     *  "Name": "mock-service",
     *  "Tags": [
     *     "mock"
     *  ],
     *  "Address": "192.168.10.10",
     *  "Port": 9090,
     *  "Meta": {
     *    "get": "/mock/get",
     *    "post": "/mock/post1, /mock/post2"
     *  },
     *  "EnableTagOverride": false,
     *  "Check": {
     *      "DeregisterCriticalServiceAfter": "90m",
     *      "HTTP": "http://192.168.10.10:9090/mock/cmd?config=current",
     *      "Interval": "30s"
     *    }
     *  }
     * @param service
     *  The service associated with the payload.
     *
     * @return
     *  The payload in Json format.
     */
    public static String generatePayloadForRegister(Service service, List<Configuration> configurations) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        // ID
        buffer.append("\"ID\": \"").append(service.getId()).append("\",");
        // Name
        buffer.append("\"Name\": \"").append(service.getName()).append("\",");
        // Tags
        buffer.append("\"Tags\": [");
        buffer.append("\"mock\"");
        buffer.append("],");
        // Address
        buffer.append("\"Address\": \"").append(service.getAddress()).append("\",");
        // Port
        buffer.append("\"Port\": ").append(service.getPort()).append(",");
        // Meta
        buffer.append("\"Meta\": {");
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
        buffer.append("},");
        // EnableTagOverride
        buffer.append("\"EnableTagOverride\": false,");
        // Check
        HealthCheck healthCheck = service.getHealthCheck();
        if (healthCheck != null && healthCheck.isEnabled()) {
            buffer.append("\"Check\": {");
            buffer.append("\"DeregisterCriticalServiceAfter\": \"").append(healthCheck.getDeRegisterAfter()).append("\",");
            buffer.append("\"HTTP\": \"")
                    .append("http://")
                    .append(service.getAddress()).append(":")
                    .append(service.getPort())
                    .append("/mock/cmd?config=current")
                    .append("\",");
            buffer.append("\"Interval\": \"").append(healthCheck.getInterval()).append("\"");
            buffer.append("}");
        }
        buffer.append("}");
        return buffer.toString();
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
    protected static Map<String, List<String>> getServicesByType(List<Configuration> configurations) {
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
