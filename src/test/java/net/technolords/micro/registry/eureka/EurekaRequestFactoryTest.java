package net.technolords.micro.registry.eureka;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.technolords.micro.model.jaxb.registration.Registration;
import net.technolords.micro.model.jaxb.registration.Service;
import net.technolords.micro.test.factory.ConfigurationsFactory;

public class EurekaRequestFactoryTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATASET_FOR_REGISTER = "dataSetForRegister";

    @Test
    public void testCreateRegisterRequest() {
        Registration registration = this.createRegistration("mock-service", "mock-1", "localhost", 9090);
        HttpPost httpPost = (HttpPost) EurekaRequestFactory.createRegisterRequest(registration, ConfigurationsFactory.createConfigurations());
        Assert.assertNotNull(httpPost);
    }

    /**
     * - service name
     * - service instance
     * - host
     * - port
     * - expected
     *
     * @return
     */
    @DataProvider(name = DATASET_FOR_REGISTER)
    public Object[][] dataSetMock(){
        return new Object[][] {
                { "mock-service", "mock-1", "localhost", 9090, "http://localhost:9090/eureka/v2/apps/mock-service"},
        };
    }

    @Test (dataProvider = DATASET_FOR_REGISTER)
    public void testGenerateUrlForRegister(String name, String id, String host, int port, String expected) {
        Registration registration = this.createRegistration(name, id, host, port);
        String actual = EurekaRequestFactory.generateUrlForRegister(registration);
        Assert.assertEquals(expected, actual);
    }

    protected Registration createRegistration(String name, String id, String host, int port) {
        Registration registration = new Registration();
        registration.setAddress(host);
        registration.setPort(port);
        Service service = new Service();
        service.setName(name);
        service.setId(id);
        registration.setService(service);
        return registration;
    }

}