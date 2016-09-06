# Microservice mock
This micro service represents a configurable webservice that can mock any other web service, by means of configuration.
When a request is made to the mock service, it checks the configuration for a matching URI. When a match is found,
the associated respond from the configuration is returned.

## Mock Configuration
The configuration is XML based, and must be compliant against a XSD. The schema is defined as:

    <xs:schema
        xmlns="http://xsd.technolords.net"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        targetNamespace="http://xsd.technolords.net"
        elementFormDefault="qualified"
    >

Todo, delay etc

## Log Configuration

Todo, usage, example, ref

## Roadmap

Todo

