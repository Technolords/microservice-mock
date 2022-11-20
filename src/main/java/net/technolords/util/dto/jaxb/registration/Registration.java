package net.technolords.util.dto.jaxb.registration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;

public class Registration {
    private Registrar registrar;
    private String address;
    private int port;
    private Service service;

    @XmlEnum
    public enum Registrar { CONSUL, EUREKA }

    @XmlAttribute(name = "registrar", required = true)
    public Registrar getRegistrar() {
        return registrar;
    }

    public void setRegistrar(Registrar registrar) {
        this.registrar = registrar;
    }

    @XmlAttribute(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlAttribute(name = "port")
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @XmlElement(name = "service")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
