package net.technolords.micro.config.jaxb.namespace;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class NamespaceList {
    private List<NamespaceConfig> namespaces;

    @XmlElement(name = "namespace")
    public List<NamespaceConfig> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<NamespaceConfig> namespaces) {
        this.namespaces = namespaces;
    }
}
