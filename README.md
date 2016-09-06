# Microservice mock
This micro service represents a configurable webservice that can mock any other web service, by means of configuration.
When a request is made to the mock service, it checks the configuration for a matching URI. When a match is found,
the associated response from the configuration is returned.

## Usage
The java jar is executable, and looks like this (depending on the version):

    java -jar target/microservice-mock-1.0.0-SNAPSHOT.jar

Most of the logging is suppressed, however that is fully configurable as well. For more details see [here](https://github.com/Technolords/microservice-mock#log-configuration).
Out of the box, the log is emitted to the standard output, and there is no log file per default. Snippets of the log output:

    2016-09-06 21:38:09,317 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  About to validate the configuration...
    2016-09-06 21:38:09,345 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  ... valid, proceeding...
    2016-09-06 21:38:09,345 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  About to initialize the configuration...
    2016-09-06 21:38:09,450 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  ... done, URL mappings parsed [1 for POST, 1 for GET]

The log shows how many mappings (URI with response config) has been recognized. Note that it makes a distinction between POST and GET.

    2016-09-06 21:38:09,969 [INFO] [main] [org.apache.camel.impl.DefaultCamelContext] INFO  Apache Camel 2.17.1 (CamelContext: camel-1) started in 0.342 seconds

The log shows the final log statement which means the mock service is started and ready to receive requests.

## Mock Configuration
The configuration is XML based, and must be compliant against a XSD. See for the schema [here](https://github.com/Technolords/microservice-mock#xsd-schema).
### Usage
Provide a java system property to the command line as follow:

    java -Dconfig=/var/data/mock-configuration.xml -jar target/microservice-mock-1.0.0-SNAPSHOT.jar

During startup, the log shows something like this:

    2016-09-06 21:52:10,945 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  Using configuration file: /var/data/mock-configuration.xml
    2016-09-06 21:52:10,948 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  File exist: true

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


