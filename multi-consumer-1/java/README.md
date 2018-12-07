# Send and Receive Messages in Java using Azure Event Hubs for Apache Kafka Ecosystems

NOTE: this sample is based on MS Azure Quickstart 'azure-event-hubs-for-kafka' (here: <https://github.com/Azure/azure-event-hubs-for-kafka/tree/master/quickstart/java>). It has been enhanced with command line options, log4j configuration, better management of multi-threading for producer and proper closing of kafka consumers.

This sample allows to connect either to a native Kafka broker or to Azure Event Hubs service. This way it provides an easy way to run the same scenarios against both infrastructure and compare behaviours.

## Azure Event Hubs

### Create an Event Hubs namespace

An Event Hubs namespace is required to send or receive from any Event Hubs service. See [Create Kafka-enabled Event Hubs](https://docs.microsoft.com/azure/event-hubs/event-hubs-create-kafka-enabled) for instructions on getting an Event Hubs Kafka endpoint. Make sure to copy the Event Hubs connection string for later use.

#### FQDN

For this sample, you will need the connection string from the portal as well as the FQDN that points to your Event Hub namespace. **The FQDN can be found within your connection string as follows**:

```
Endpoint=sb://{YOUR.EVENTHUBS.FQDN}/;SharedAccessKeyName={SHARED.ACCESS.KEY.NAME};SharedAccessKey={SHARED.ACCESS.KEY}
```

## Producer

Using the provided producer example, send messages to the Event Hubs service. To change the Kafka version, change the dependency in the pom file to the desired version.

### Provide an Event Hubs Kafka endpoint

#### producer.config

Update the `bootstrap.servers` and `sasl.jaas.config` values in `producer/src/main/resources/producer.config` to direct the producer to the Event Hubs Kafka endpoint with the correct authentication.

```config
bootstrap.servers={YOUR.EVENTHUBS.FQDN}:9093
security.protocol=SASL_SSL
sasl.mechanism=PLAIN
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="$ConnectionString" password="{YOUR.EVENTHUBS.CONNECTION.STRING}";
```

### Run producer from command line

To run the producer from the command line, generate the JAR and then run from within Maven (alternatively, generate the JAR using Maven, then run in Java by adding the necessary Kafka JAR(s) to the classpath):

```bash
mvn clean package
mvn exec:java -Dexec.mainClass="TestProducer"
```

The producer will now begin sending events to the Kafka-enabled Event Hub at topics `topic-aa` and `topic-bb` and printing the events to stdout. If you would like to change the topic, change the TOPIC constant in `producer/src/main/java/com/example/app/TestProducer.java`.

## Consumer

Using the provided consumer example, receive messages from the Kafka-enabled Event Hubs or from a Kafka broker.

### Consumer Config

#### Azure Event Hubs (with option --mode azure-eh)

Change the `bootstrap.servers` and `sasl.jaas.config` values in `consumer/src/main/resources/rjtest/rjtest.azure.eventhubs.consumer.config` to direct the consumer to the Event Hubs endpoint with the correct authentication.

```config
bootstrap.servers={YOUR.EVENTHUBS.FQDN}:9093
security.protocol=SASL_SSL
sasl.mechanism=PLAIN
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="$ConnectionString" password="{YOUR.EVENTHUBS.CONNECTION.STRING}";
```

#### Kafka broker (with option --mode native or no mode specified)

If needed, update the `bootstrap.servers` value in `consumer/src/main/resources/rjtest/rjtest.native.consumer.config`.

### Run consumer from command line

To run the producer from the command line, generate the JAR and then run from within Maven (alternatively, generate the JAR using Maven, then run in Java by adding the necessary Kafka JAR(s) to the classpath):

```bash
mvn clean package
rjtest-kafka-consumer.sh --help
```

e.g. to consume messages from 2 topics hosted by Azure Event Hubs with a particular consumer group:

```bash
rjtest-kafka-consumer.sh --mode azure-eh -i 0 -t topic-aa,topic-bb --group rjtest-cg1
```