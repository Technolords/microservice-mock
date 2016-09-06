# Microservice mock
This micro service represents a configurable webservice that can mock any other web service, by means of configuration.
When a request is made to the mock service, it checks the configuration for a matching URI. When a match is found,
the associated respond from the configuration is returned.

## Mock Configuration
The configuration is XML based, and must be compliant against a XSD. See for the schema [here](https://github.com/Technolords/microservice-mock#xsd-schema)

Todo, delay etc

## Log Configuration

Todo, usage, example, ref

## Roadmap

Todo

## XSD Schema

    <xs:schema
        xmlns="http://xsd.technolords.net"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        targetNamespace="http://xsd.technolords.net"
        elementFormDefault="qualified"
    >

        <xs:element name="configurations">
            <xs:complexType>
                <xs:sequence>
                    <xs:element name="configuration" type="configurationType" minOccurs="0" maxOccurs="unbounded" />
                </xs:sequence>
            </xs:complexType>
        </xs:element>

        <xs:simpleType name="httpRequestType">
            <xs:restriction base="xs:string">
                <xs:enumeration value="GET"/>
                <xs:enumeration value="POST"/>
            </xs:restriction>
        </xs:simpleType>

        <xs:complexType name="configurationType">
            <xs:sequence>
                <xs:element name="resource" type="resourceType" minOccurs="0" maxOccurs="unbounded" />
                <xs:element name="namespaces" type="namespacesType" minOccurs="0" maxOccurs="unbounded" />
                <xs:element name="resource-groups" type="resourceGroupsType" minOccurs="0" maxOccurs="unbounded" />
            </xs:sequence>
            <xs:attribute name="type" type="httpRequestType" use="required" />
            <xs:attribute name="url" type="xs:string" use="required" />
        </xs:complexType>

        <xs:complexType name="namespacesType">
            <xs:sequence>
                <xs:element name="namespace" type="namespaceType" minOccurs="1" maxOccurs="unbounded" />
            </xs:sequence>
        </xs:complexType>

        <xs:complexType name="namespaceType">
            <xs:simpleContent>
                <xs:extension base="xs:string">
                    <xs:attribute name="prefix" type="xs:string" use="required" />
                </xs:extension>
            </xs:simpleContent>
        </xs:complexType>

        <xs:complexType name="resourceGroupsType">
            <xs:sequence>
                <xs:element name="resource-group" type="resourceGroupType" minOccurs="1" maxOccurs="unbounded" />
            </xs:sequence>
        </xs:complexType>

        <xs:complexType name="resourceGroupType">
            <xs:sequence>
                <xs:element name="xpath" type="xpathType" />
                <xs:element name="resource" type="resourceType" />
            </xs:sequence>
        </xs:complexType>

        <xs:complexType name="resourceType">
            <xs:simpleContent>
                <xs:extension base="xs:string">
                    <xs:attribute name="delay" type="xs:string" use="optional" />
                    <xs:attribute name="error-code" type="xs:string" use="optional" />
                    <xs:attribute name="error-rate" type="xs:integer" use="optional" />
                </xs:extension>
            </xs:simpleContent>
        </xs:complexType>

        <xs:complexType name="xpathType">
            <xs:simpleContent>
                <xs:extension base="xs:string" />
            </xs:simpleContent>
        </xs:complexType>
    </xs:schema>


