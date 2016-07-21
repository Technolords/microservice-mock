package net.technolords.micro.config.jaxb.resource;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by Technolords on 2016-Jul-21.
 */
public class Simple {
    private String resource;
    private String cachedData;

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
}
