#!/bin/sh

/usr/bin/kafka-mirror-maker --consumer.config /tmp/mirror_conf/source-kafka.config --producer.config /tmp/mirror_conf/mirror-eventhub.config --whitelist=".*"