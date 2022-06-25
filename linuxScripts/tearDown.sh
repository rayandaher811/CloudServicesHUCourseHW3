#!/bin/bash

cd ../
sudo docker-compose down --remove-orphans
cd -
./clean_env.sh
rm -rf ../data/