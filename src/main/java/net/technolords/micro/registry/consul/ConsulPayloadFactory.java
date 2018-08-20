package net.technolords.micro.registry.consul;

import java.util.List;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.HealthCheck;
import net.technolords.micro.model.jaxb.registration.Service;
import net.technolords.micro.registry.util.MetadataHelper;

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
        MetadataHelper.addMetadataEntries(buffer, configurations);
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

}
