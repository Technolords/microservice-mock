package net.technolords.micro.config.jaxb.resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by Technolords on 2016-Jul-21.
 */
public class SimpleResource {
    private String resource;
    private String cachedData;
    private int delay;
    private int errorCode;
    private int errorRate;

    @XmlValue
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @XmlTransient
    public String getCachedData() {
        return cachedData;
    }

    public void setCachedData(String cachedData) {
        this.cachedData = cachedData;
    }

    @XmlAttribute(name = "delay")
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @XmlAttribute(name = "error-code")
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @XmlAttribute(name = "error-rate")
    public int getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }
}
