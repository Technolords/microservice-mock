package net.technolords.micro.model.jaxb.registration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Consul {
    private String address;
    private int port;
    private Service service;

    @XmlAttribute (name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlAttribute (name = "port")
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @XmlElement (name = "service")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
