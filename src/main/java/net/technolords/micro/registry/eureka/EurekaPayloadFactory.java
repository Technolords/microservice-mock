package net.technolords.micro.registry.eureka;

import java.util.List;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.Service;
import net.technolords.micro.registry.util.MetadataHelper;

public class EurekaPayloadFactory {

    /**
     * Create a payload like:
     *
     * {
     *     "instance": {
     *         "hostName": "mock1",                                                        instanceId
     *         "app": "mock",                                                              appId
     *         "ipAddr": "10.0.0.10",
     *         "port": {"$": "8080", "@enabled": "true"},
     *         "status": "UP",
     *         "securePort": {"$": "8443", "@enabled": "true"},
     *         "healthCheckUrl": "http://192.168.10.10:9090/mock/cmd?config=current",
     *         "dataCenterInfo": {
     *             "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
     *             "name": "MyOwn"
     *         },
     *         "metadata": {
     *         	"get": "/mock/get",
     *     		"post": "/mock/post1, /mock/post2"
     *         }
     *     }
     * }
     *
     * @param service
     *  The service associated with the payload.
     *
     * @return
     *  The payload in Json format.
     */
    public static String generatePayloadForRegister(Service service, List<Configuration> configurations) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        buffer.append("\"instance\": {");
        // hostname
        buffer.append("\"hostName\": \"").append(service.getId()).append("\",");
        // app
        buffer.append("\"app\": \"").append(service.getName()).append("\",");
        // ipAddr
        buffer.append("\"ipAddr\": \"").append(service.getAddress()).append("\",");
        // port
        buffer.append("\"port\": {");
            buffer.append("\"$\": \"").append(service.getPort()).append("\", ");
            buffer.append("\"@enabled\": \"true\"");
        buffer.append("},");                                                        // port
        // status
        buffer.append("\"status\": \"UP\",");
        // securePort
        buffer.append("\"securePort\": {");
        buffer.append("\"$\": \"8443\", ");
        buffer.append("\"@enabled\": \"true\"");
        buffer.append("},");                                                        // securePort
        // healthCheckUrl
        buffer.append("\"healthCheckUrl\": \"http://");
        buffer.append(service.getAddress()).append(":").append(service.getPort());
        buffer.append("/mock/cmd?config=current\", ");
        // dataCenterInfo
        buffer.append("\"dataCenterInfo\": {");
            buffer.append("\"@class\": \"").append("com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo").append("\", ");
            buffer.append("\"name\": \"MyOwn\"");
        buffer.append("},");                                                        // dataCenterInfo
        // metadata
        buffer.append("\"metadata\": {");
        MetadataHelper.addMetadataEntries(buffer, configurations);
        buffer.append("}");                                                         // metadata
        buffer.append("}");                                                         // instance
        buffer.append("}");
        return buffer.toString();
    }
}
