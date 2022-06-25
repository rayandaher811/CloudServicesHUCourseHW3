#!/bin/bash

echo "Cleaning docker-compose data"
# remove docker data
sudo rm -rf ../data/zookeeper-data/*
sudo rm -rf ../data/zookeeper-logs/*

echo "Cleaning docker engine trash"
sudo docker volume rm $(sudo docker volume ls) >/dev/null 2>&1
sudo docker network rm $(sudo docker network ls) >/dev/null 2>&1

echo "Cleaning topics listener git repo"
# clean git repos data
cd ../TopicsListener/
gradle --warning-mode none clean
git clean -xdf . >/dev/null 2>&1
cd -

echo "Cleaning kafka streams pipe git repo"
cd ../KafkaStreamsPipe/
mvn -q clean
git clean -xdf . >/dev/null 2>&1
cd -