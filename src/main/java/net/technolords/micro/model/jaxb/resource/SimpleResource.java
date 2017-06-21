package net.technolords.micro.model.jaxb.resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

public class SimpleResource {
    private String resource;
    private String cachedData;
    private int delay;
    private String errorCode;
    private int errorRate;
    private String contentType;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimpleResource)) {
            return false;
        }
        SimpleResource ref = (SimpleResource) obj;
        return (ref.getResource().equals(this.getResource())
                && ref.getCachedData().equals(this.getCachedData())
                && ref.getContentType().equals(this.getContentType())
                && ref.getErrorCode().equals(this.getErrorCode())
 );
    }

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
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @XmlAttribute(name = "error-rate")
    public int getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }

    @XmlAttribute(name = "content-type")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}