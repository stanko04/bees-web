#!/bin/bash
#nohup java -jar /path//
nohup ./mvnw spring-boot:run > log.txt > 2>&1 &
echo $! > ./pid.file