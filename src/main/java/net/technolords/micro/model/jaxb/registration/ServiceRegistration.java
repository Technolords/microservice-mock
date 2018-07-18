package net.technolords.micro.model.jaxb.registration;

import javax.xml.bind.annotation.XmlElement;

public class ServiceRegistration {
    private Consul consul;

    @XmlElement (name = "consul")
    public Consul getConsul() {
        return consul;
    }

    public void setConsul(Consul consul) {
        this.consul = consul;
    }
}
