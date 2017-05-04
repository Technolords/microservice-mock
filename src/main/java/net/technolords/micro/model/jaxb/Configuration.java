package net.technolords.micro.model.jaxb;

import net.technolords.micro.model.jaxb.namespace.NamespaceList;
import net.technolords.micro.model.jaxb.resource.ResourceGroups;
import net.technolords.micro.model.jaxb.resource.SimpleResource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Map;
import java.util.regex.Pattern;

public class Configuration {
    private String type;
    private String url;
    private SimpleResource simpleResource;
    private ResourceGroups resourceGroups;
    private NamespaceList namespaceList;
    private Map<String, String> cachedNamespaceMapping;
    private Pattern pattern;

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof Configuration)) {
            return false;
        }
        Configuration ref = (Configuration) obj;
        return (ref.getUrl().equals(this.getUrl()) && ref.getType().equals(this.getType()) && ref.getSimpleResource().equals(this.getSimpleResource()));
    }

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement(name = "resource")
    public SimpleResource getSimpleResource() {
        return simpleResource;
    }

    public void setSimpleResource(SimpleResource simpleResource) {
        this.simpleResource = simpleResource;
    }

    @XmlElement(name = "resource-groups")
    public ResourceGroups getResourceGroups() {
        return resourceGroups;
    }

    public void setResourceGroups(ResourceGroups resourceGroups) {
        this.resourceGroups = resourceGroups;
    }

    @XmlElement(name = "namespaces")
    public NamespaceList getNamespaceList() {
        return namespaceList;
    }

    public void setNamespaceList(NamespaceList namespaceList) {
        this.namespaceList = namespaceList;
    }

    @XmlTransient
    public Map<String, String> getCachedNamespaceMapping() {
        return cachedNamespaceMapping;
    }

    public void setCachedNamespaceMapping(Map<String, String> cachedNamespaceMapping) {
        this.cachedNamespaceMapping = cachedNamespaceMapping;
    }

    @XmlTransient
    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
