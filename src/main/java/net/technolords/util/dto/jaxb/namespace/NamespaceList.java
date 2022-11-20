package net.technolords.util.dto.jaxb.namespace;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class NamespaceList {
    private List<NamespaceConfig> namespaces;

    public NamespaceList() {
        this.namespaces = new ArrayList<>();
    }

    @XmlElement(name = "namespace")
    public List<NamespaceConfig> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<NamespaceConfig> namespaces) {
        this.namespaces = namespaces;
    }
}
