package net.technolords.micro.registry.eureka;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.Registration;

public class EurekaRequestFactory {
    private static final String API_SERVICE_REGISTER = "/eureka/v2/apps/";
    private static final String API_SERVICE_DEREGISTER = "/eureka/v2/apps/";

    /**
     * A factory for a HttpPost method, suitable to register the service.
     *
     * @param registration
     *  The Registration reference associated with the HttpPost
     *
     * @return
     *  The generated httpPost
     */
    public static HttpEntityEnclosingRequestBase createRegisterRequest(Registration registration, List<Configuration> configurations) {
        HttpPost httpPost = new HttpPost(generateUrlForRegister(registration));
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setEntity(new StringEntity(EurekaPayloadFactory.generatePayloadForRegister(registration.getService(), configurations), "UTF-8"));
        return httpPost;
    }

    /**
     * http://localhost:8080/eureka/v2/apps/:appId (POST)
     *
     * @param registration
     *  The Registration associated with the URL
     *
     * @return
     *  The generated URL in string format
     */
    protected static String generateUrlForRegister(Registration registration) {
        StringBuffer buffer = new StringBuffer();
        if (!registration.getAddress().startsWith("http")) {
            buffer.append("http://");
        }
        buffer.append(registration.getAddress()).append(":").append(registration.getPort());
        buffer.append(API_SERVICE_REGISTER);
        buffer.append(registration.getService().getName());
        return buffer.toString();
    }

    /**
     * A factory for a HttpDelete method, suitable to register the service.
     *
     * @param registration
     *  The Registration reference associated with the HttpDelete
     *
     * @return
     *  The generated httpDelete
     */
    public static HttpRequestBase createDeRegisterRequest(Registration registration, List<Configuration> configurations) {
        HttpDelete httpDelete = new HttpDelete(generatedUrlForDeregister(registration));
        return httpDelete;
    }

    /**
     * http://localhost:8080/eureka/v2/apps/:appId/:instanceId (DELETE)
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
        buffer.append(registration.getService().getName());
        buffer.append("/");
        buffer.append(registration.getService().getId());
        return buffer.toString();
    }
}
