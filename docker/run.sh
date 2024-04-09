#!/bin/bash

cd ../
./gradlew clean build shadowJar
cd docker
docker compose up