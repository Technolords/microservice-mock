package net.technolords.micro.registry.consul;

import java.util.List;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.Registration;

public class ConsulRequestFactory {
    private static final String API_SERVICE_REGISTER = "/v1/agent/service/register";
    private static final String API_SERVICE_DEREGISTER = "/v1/agent/service/deregister/";

    /**
     * A factory for a HttpPut method, suitable to register the service.
     *
     * @param registration
     *  The Registration reference associated with the HttpPut
     *
     * @return
     *  The generated HttpPut
     */
    public static HttpEntityEnclosingRequestBase createRegisterRequest(Registration registration, List<Configuration> configurations) {
        HttpPut httpPut = new HttpPut(generateUrlForRegister(registration));
        httpPut.setEntity(new StringEntity(ConsulPayloadFactory.generatePayloadForRegister(registration.getService(), configurations), "UTF-8"));
        return httpPut;
    }

    /**
     * http://192.168.10.14:8500/v1/agent/service/register (PUT)
     *
     * @param registration
     *  The Registration associated with the URL
     *
     * @return
     *  The generated URL in string format
     */
    private static String generateUrlForRegister(Registration registration) {
        StringBuffer buffer = new StringBuffer();
        if (!registration.getAddress().startsWith("http")) {
            buffer.append("http://");
        }
        buffer.append(registration.getAddress()).append(":").append(registration.getPort());
        buffer.append(API_SERVICE_REGISTER);
        return buffer.toString();
    }

    /**
     * A factory for a HttpPut method, suitable to deregister the service.
     *
     * @param registration
     *  The consul reference associated with the HttpPut
     *
     * @return
     *  The generated HttpPut
     */
    public static HttpEntityEnclosingRequestBase createDeregisterRequest(Registration registration) {
        return new HttpPut(generatedUrlForDeregister(registration));
    }

    /**
     * http://192.168.10.14:8500/v1/agent/service/deregister/:serviceId (PUT)
     *
     * @param registration
     *  The service associated with the URL
     *
     * @return
     *  The generated URL in string format
     */
    private static String generatedUrlForDeregister(Registration registration) {
        StringBuffer buffer = new StringBuffer();
        if (!registration.getAddress().startsWith("http")) {
            buffer.append("http://");
        }
        buffer.append(registration.getAddress()).append(":").append(registration.getPort());
        buffer.append(API_SERVICE_DEREGISTER);
        buffer.append(registration.getService().getId());
        return buffer.toString();
    }

}
