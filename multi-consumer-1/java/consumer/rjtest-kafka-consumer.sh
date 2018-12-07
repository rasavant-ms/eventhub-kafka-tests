#!/bin/bash 

mvn exec:java -Dexec.mainClass="org.rjtest.test.azure.eventhubs.kafka.TestConsumerExt" -Dexec.args="$*"