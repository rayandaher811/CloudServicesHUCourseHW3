#!/bin/bash

# Copying jar of pipes
cd ../KafkaStreamsPipe/
mvn clean package
cd -
cp ../KafkaStreamsPipe/target/KafkaStreamsPipe-jar-with-dependencies.jar ../lib/


# Copying jar of "gui"
cd ../TopicsListener
gradle build
cd -
cp ../TopicsListener/build/libs/TopicsListener-1.0-all.jar ../lib/