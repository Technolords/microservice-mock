package net.technolords.micro.registry.consul;

import java.util.List;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.Consul;
import net.technolords.micro.model.jaxb.registration.Service;

public class ConsulRequestFactory {
    private static final String API_SERVICE_REGISTER = "/v1/agent/service/register";
    private static final String API_SERVICE_DEREGISTER = "/v1/agent/service/deregister/";

    /**
     * A factory for a HttpPut method, suitable to register the service.
     *
     * @param consul
     *  The consul reference associated with the HttpPut
     *
     * @return
     *  The generated HttpPut
     */
    public static HttpEntityEnclosingRequestBase createRegisterRequest(Consul consul, List<Configuration> configurations) {
        HttpPut httpPut = new HttpPut(generateUrlForRegister(consul));
        httpPut.setEntity(new StringEntity(ConsulPayloadFactory.generatePayloadForRegister(consul.getService(), configurations), "UTF-8"));
        return httpPut;
    }

    /**
     * http://192.168.10.14:8500/v1/agent/service/register (PUT)
     *
     * @param consul
     *  The Consul associated with the URL
     *
     * @return
     *  The generated URL in string format
     */
    private static String generateUrlForRegister(Consul consul) {
        StringBuffer buffer = new StringBuffer();
        if (!consul.getAddress().startsWith("http")) {
            buffer.append("http://");
        }
        buffer.append(consul.getAddress()).append(":").append(consul.getPort());
        buffer.append(API_SERVICE_REGISTER);
        return buffer.toString();
    }

    /**
     * A factory for a HttpPut method, suitable to deregister the service.
     *
     * @param consul
     *  The consul reference associated with the HttpPut
     *
     * @return
     *  The generated HttpPut
     */
    public static HttpEntityEnclosingRequestBase createDeregisterRequest(Consul consul) {
        return new HttpPut(generatedUrlForDeregister(consul));
    }

    /**
     * http://192.168.10.14:8500/v1/agent/service/deregister/:serviceId (PUT)
     *
     * @param consul
     *  The service associated with the URL
     *
     * @return
     *  The generated URL in string format
     */
    private static String generatedUrlForDeregister(Consul consul) {
        StringBuffer buffer = new StringBuffer();
        if (!consul.getAddress().startsWith("http")) {
            buffer.append("http://");
        }
        buffer.append(consul.getAddress()).append(":").append(consul.getPort());
        buffer.append(API_SERVICE_DEREGISTER);
        buffer.append(consul.getService().getId());
        return buffer.toString();
    }

}
