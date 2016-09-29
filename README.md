# Microservice mock
This micro service represents a configurable webservice that can mock any other web service by means of configuration.
When a request is made to the mock service, it checks the configuration for a matching URI. When a match is found,
the associated response from the configuration is returned.

## Usage
The java jar is executable (required java version is 1.8), and looks like this (depending on the version):

    java -jar target/microservice-mock-1.0.0.jar

Most of the logging is suppressed by design, however it is fully configurable as well. For more details see [here](https://github.com/Technolords/microservice-mock#log-configuration).
Out of the box, the log is emitted to the standard output, and there is no log file per default. Snippets of the log output:

    2016-09-06 21:38:09,317 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  About to validate the configuration...
    2016-09-06 21:38:09,345 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  ... valid, proceeding...
    2016-09-06 21:38:09,345 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  About to initialize the configuration...
    2016-09-06 21:38:09,450 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  ... done, URL mappings parsed [1 for POST, 1 for GET]

The log shows how many mappings (URI with response config) has been recognized. Note that it makes a distinction between POST and GET.

    2016-09-06 21:38:09,969 [INFO] [main] [org.apache.camel.impl.DefaultCamelContext] INFO  Apache Camel 2.17.1 (CamelContext: camel-1) started in 0.342 seconds

The log shows the final log statement which means the mock service is started and ready to receive requests.

### Port Configuration
Provide a Java system property to the command line as follow:

    java -Dport=9999 -jar target/microservice-mock-1.0.0.jar

The log confirms the port with:

    2016-09-06 22:31:58,235 [INFO] [main] [net.technolords.micro.route.RestServiceRoute] INFO  Using port: 9999

## Mock Configuration
The configuration is XML based, and must be compliant against a XSD. See for the schema [here](https://github.com/Technolords/microservice-mock#xsd-schema).
### Usage
Provide a Java system property to the command line as follow:

    java -Dconfig=/var/data/mock-configuration.xml -jar target/microservice-mock-1.0.0.jar

During startup, the log shows something like this:

    2016-09-06 21:52:10,945 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  Using configuration file: /var/data/mock-configuration.xml
    2016-09-06 21:52:10,948 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  File exist: true

### Example of a configuration file
The XML configuration file must have a namespace according to the [schema](https://github.com/Technolords/microservice-mock#xsd-schema).

    <configurations xmlns="http://xsd.technolords.net">

        <configuration type="GET" url="/mock/sample1">
            <resource>sample1.json</resource>
        </configuration>
        <configuration type="GET" url="/mock/sample2">
            <resource>sample2.xml</resource>
        </configuration>

        <configuration type="POST" url="/mock/post">
            <namespaces>
                <namespace prefix="technolords">urn:some:reference:1.0</namespace>
            </namespaces>
            <resource-groups>
                <resource-group>
                    <xpath>/technolords:sample/technolords:message[@id = '1']</xpath>
                    <resource>mock/sample-post1.json</resource>
                </resource-group>
                <resource-group>
                    <xpath>/technolords:sample/technolords:message[@id = '2']</xpath>
                    <resource delay="10000">mock/sample-post2.json</resource>
                </resource-group>
                <resource-group>
                    <xpath>/technolords:sample/technolords:message[@id = '3']</xpath>
                    <resource error-code="206" error-rate="50">mock/sample-post3.json</resource>
                </resource-group>
                <resource-group>
                    <xpath>/technolords:sample/technolords:message[@id = '4']</xpath>
                    <resource content-type="text/plain">mock/sample-post4.txt</resource>
                </resource-group>
            </resource-groups>
        </configuration>
    </configurations>

The configuration above lists two configurations for a GET requests, and one for a POST request. However, since
the POST is about the body, in this case XML, associated xpath expressions are present for finer grained configuration.

Example POST message:

    <sample xmlns="urn:some:reference:1.0">
        <message id="1"/>
    </sample>

When the message above is posted, the content of the file 'sample-post1.json' is cached and then returned. Repeated
requests will then return the cached result (to enhance performance). This makes the mock service an ideal tool to
support load testing as well.

If a request is made which does not match any url's (mappings) a 404 is returned.

### Data configuration
The resource files, associated with the responses, are relative from the data folder. To configure the data folder, a
Java system property to the command line as follow must submitted:

    java -Ddata=/var/data/mock -Dconfig=/var/data/mock-configuration.xml -jar target/microservice-mock-1.0.0.jar

The log confirms with:

    2016-09-06 22:34:42,274 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  Using data folder: /var/data/mock
    2016-09-06 22:34:42,275 [INFO] [main] [net.technolords.micro.config.ConfigurationManager] INFO  Folder exist: true, and is folder: true

### Optional attributes
The XML configuration mentions some optional attributes, which are:
* content-type: which is similar to the mime-type, it defines the data type of the response. This defaults to 'application/json'.
* delay: which is exactly what it means, it adds a delay in the response measured in milli seconds.
* error: which allows simulation of erroneous responses, based on a percentage (error rate) and the alternative response code (error-code).

## Log Configuration
The code uses a logging library called 'logback' and has an embedded (default) configuration. To use a custom log configuration a Java system property must be provided:

    java -Dlogback.configurationFile=/path/to/config.xml -jar target/microservice-mock-1.0.0.jar

To understand the options of the configuration file, see the remote documentation [here](http://logback.qos.ch/manual/configuration.html).

## Roadmap

The following improvements are planned. See [this roadmap](https://github.com/Technolords/microservice-mock/projects/1)

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


