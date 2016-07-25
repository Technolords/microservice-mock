package net.technolords.micro.config.jaxb;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import net.technolords.micro.config.jaxb.namespace.NamespaceList;
import net.technolords.micro.config.jaxb.resource.Group;
import net.technolords.micro.config.jaxb.resource.Simple;

/**
 * Created by Technolords on 2016-Jul-20.
 */
public class Configuration {
    private String type;
    private String url;
    private Simple getResource;
    private Group postResources;
    private NamespaceList namespaceList;
    private Map<String, String> cachedNamespaceMapping;

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
    public Simple getGetResource() {
        return getResource;
    }

    public void setGetResource(Simple getResource) {
        this.getResource = getResource;
    }

    @XmlElement(name = "resource-group")
    public Group getPostResources() {
        return postResources;
    }

    public void setPostResources(Group postResources) {
        this.postResources = postResources;
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
}
