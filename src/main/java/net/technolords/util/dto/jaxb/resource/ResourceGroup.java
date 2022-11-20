package net.technolords.util.dto.jaxb.resource;

import javax.xml.bind.annotation.XmlElement;

public class ResourceGroup {
    private XpathConfig xpathConfig;
    private JsonpathConfig jsonpathConfig;
    private SimpleResource simpleResource;

    @XmlElement(name = "xpath")
    public XpathConfig getXpathConfig() {
        return xpathConfig;
    }

    public void setXpathConfig(XpathConfig xpathConfig) {
        this.xpathConfig = xpathConfig;
    }

    @XmlElement(name = "jsonpath")
    public JsonpathConfig getJsonpathConfig() {
        return jsonpathConfig;
    }

    public void setJsonpathConfig(JsonpathConfig jsonpathConfig) {
        this.jsonpathConfig = jsonpathConfig;
    }

    @XmlElement(name = "resource")
    public SimpleResource getSimpleResource() {
        return simpleResource;
    }

    public void setSimpleResource(SimpleResource simpleResource) {
        this.simpleResource = simpleResource;
    }
}
