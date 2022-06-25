#!/bin/bash

cd ../
# setting docker env
mkdir -p data/zookeeper-data/
mkdir -p data/zookeeper-logs/

# Running the whole kafka cluster and waiting till all topics has been created
sudo docker-compose up -d
echo "please wait while creating all kafka topics please wait, while it's coming up, building all are apps"
cd -

# Installing the wiki to kafka dependecies
echo "Installing node js app, which communicates with wiki's servers"
cd ../WikiToKafkaComponents
npm install
cd -

echo "Installing kafka streams pipe & topics listener"
# Install jars
./build_jars.sh

echo "Running node wiki's communication app:"
: Running the wiki to kafka script via node
cd ../WikiToKafkaComponents/
node WikiToKafka.js >/dev/null 2>&1 & 
cd -

echo "Running kafka streams jar:"
java -jar ../lib/KafkaStreamsPipe-jar-with-dependencies.jar >/dev/null 2>&1 &

echo "Running gui "UI":"
java -jar ../lib/TopicsListener-1.0-all.jar

./tearDown.sh