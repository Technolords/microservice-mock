package net.technolords.util.dto.jaxb.query;

import net.technolords.util.dto.jaxb.resource.SimpleResource;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class QueryGroup {
    private List<QueryParameter> queryParameters;
    private SimpleResource simpleResource;

    public QueryGroup() {
        this.queryParameters = new ArrayList<>();
    }

    @XmlElement(name = "query-parameter")
    public List<QueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(List<QueryParameter> queryParameters) {
        this.queryParameters = queryParameters;
    }

    @XmlElement(name = "resource")
    public SimpleResource getSimpleResource() {
        return simpleResource;
    }

    public void setSimpleResource(SimpleResource simpleResource) {
        this.simpleResource = simpleResource;
    }
}
