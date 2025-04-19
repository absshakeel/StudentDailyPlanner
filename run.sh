#!/bin/bash

# Compile all Java files with dependencies
javac -cp .:jcalendar-1.4.jar:mysql-connector-j-8.0.33.jar *.java

# Run the application
java -cp .:jcalendar-1.4.jar:mysql-connector-j-8.0.33.jar Main