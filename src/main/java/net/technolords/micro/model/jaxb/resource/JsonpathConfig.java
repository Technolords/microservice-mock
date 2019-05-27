package net.technolords.micro.model.jaxb.resource;

import javax.xml.bind.annotation.XmlValue;

public class JsonpathConfig {
    private String jsonpath;

    @XmlValue
    public String getJsonpath() {
        return jsonpath;
    }

    public void setJsonpath(String jsonpath) {
        this.jsonpath = jsonpath;
    }
}
