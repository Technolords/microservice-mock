#!/bin/bash
echo "About to execute the mock micro service..."

JAVACMD="java \
 -Dprops=/etc/mock/config/mock.properties \
 -jar /etc/mock/mock.jar"

$JAVACMD
echo "Done using mock."
