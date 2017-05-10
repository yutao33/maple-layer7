#!/bin/bash
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
notify-send compile over
