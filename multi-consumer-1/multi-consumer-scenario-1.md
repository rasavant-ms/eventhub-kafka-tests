# Azure Kafka - Scenario 1

## Description

Very simple producer/consumer scenario:

* 1 Kafka producer sending messages in topics/Event Hubs (one or two, this can be specified on the command line)
* 2 Kafka topics, topic-aa and topic-bb, each split into 5 partitions
* 2 consumer groups: rjtest-cg1 with 5 Kafka consumers subscribing to topic-aa, rjtest-cg2 with 1 Kafka consumer subscribing to both topic-aa and topic-bb

                                           +--------------+
                                           |Kafka producer|
                                           +-----+---`.---+
                                               ,'      \
                                              /         `.
                                             /            \
                                           ,'              `.                   
       .----------------------------------+------------------`.------------------------------.
       |                                ,'                     \                             |
       |  .----------------------------+------.       .---------`.------------------------.  |
       |  |             topic-aa              |       |             topic-bb              |  |
       |  |+----+ +----+ +----+ +----+ +----+ |       |+----+ +----+ +----+ +----+ +----+ |  |
       |  ||    | |    | |    | |    | |    | |       ||    | |    | |    | |    | |    | |  |
       |  ||    | |    | |    | |    | |    | |       ||    | |    | |    | |    | |    | |  |
       |  || P1 | | P2 | | P3 | | P4 | | P5 | |       || P1 | | P2 | | P3 | | P4 | | P5 | |  |
       |  ||    | |    | |    | |    | |    | |       ||    | |    | |    | |    | |    | |  |
       |  ||    | |    | |    | |    | |    | |       ||    | |    | |    | |    | |    | |  |
       |  ||    | |    | |    | |    | |    | |       ||    | |    | |    | |    | |    | |  |
       |  |+-.-.._+-.-._ +-.--. +-.-.+ +-+-.+ |       |+--.-+ +--'-+ +-,--+ +-,--+ +,---+ |  |
       |  `--+----`-+..=``-..=-``-.=-`-.=+--`._       `----+----+----,'----,-'--=.-'------'  |
       |     |      |   ``-.._`--.|_`-.. `-._  `.          \    |   /    ,'  _,'             |
       `-----+------+------+--`'-..=_-.._+-.=`..-`-.--------+--+---+--=,'-,-'----------------'
             |      |      |      |  `--.:_-.:=._-._`._     |  | ,' ,'_,-'
             |      |      |      |      | `--.:=-..:=.`.   | | /_,_,'
        '''''|''''''|''''''|''''''|''''''|''''|  ``-::=..:.:'|,;;;:
        |  .-+--. .-'--. .-+--. .-+--. .-'--. |        ``-:-.<-:  |
        |  |    | |    | |    | |    | |    | |        |  |    |  |
        |  |    | |    | |    | |    | |    | |        |  |    |  |
        |  `----' `----' `----' `----' `----' |        |  `----'  |
        |                                     |        |          |
        |      consumer group: rjtest-cg1     |        |          |
        '
                                                   consumer group: rjtest-cg2

## Running test scenario against native local Kafka cluster

### Cluster Configuration

* Kafka cluster: Apache Kafka 2.0.0 (Scala 2.11 version)

No changes to default configuration files.

Kafka cluster started using following commands:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

Test topics creation:

```bash
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic topic-aa
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic topic-bb
bin/kafka-topics.sh --list --zookeeper localhost:2181
bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic <topic name>
```

To list and get details on consumer groups:

```bash
bin/kafka-consumer-groups.sh --list --bootstrap-server localhost:9092
bin/kafka-consumer-groups.sh --describe --group <consumer group name> --bootstrap-server localhost:9092
```

### Client Configuration

* Apache Kafka java client 1.1.0 library (kafka-clients maven artifact)

### Running scenario

#### Kafka consumers

Start Kafka consumers part of 'rjtest-cg1' consumer group:

```bash
rjtest-kafka-consumer.sh --id 0 -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 1 -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 2 -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 3 -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 4 -g rjtest-cg1 -t topic-aa
```

Start Kafka consumers part of 'rjtest-cg2' consumer group:

```bash
rjtest-kafka-consumer.sh --id 5 -g rjtest-cg2 -t topic-aa,topic-bb
```

#### Kafka producer

Send 100 messages to both topics:

```bash
rjtest-kafka-producer.sh -m 100 -t topic-aa,topic-bb
```

### Results

==> **OK**

Consumer group 'rjtest-cg1':

```bash
bin/kafka-consumer-groups.sh --describe --group rjtest-cg1 --bootstrap-server localhost:9092

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                 HOST            CLIENT-ID
topic-aa        4          17              17              0               KafkaExampleConsumer#4-4a8dcfe8-f572-453e-9970-51ffbd1ff7e4 /127.0.0.1      KafkaExampleConsumer#4
topic-aa        2          12              12              0               KafkaExampleConsumer#2-305ce1a8-8bda-4c6c-baab-fefd667891dc /127.0.0.1      KafkaExampleConsumer#2
topic-aa        3          14              14              0               KafkaExampleConsumer#3-dd9fe798-72f2-4a26-8ae3-6ad84153e6fb /127.0.0.1      KafkaExampleConsumer#3
topic-aa        1          27              27              0               KafkaExampleConsumer#1-4faadbad-dfc0-42de-9af2-0ab758ab9fe0 /127.0.0.1      KafkaExampleConsumer#1
topic-aa        0          30              30              0               KafkaExampleConsumer#0-2e57d5ba-3a98-49a0-98bc-50b004b62c62 /127.0.0.1      KafkaExampleConsumer#0
```

Consumer group 'rjtest-cg2':

```bash
bin/kafka-consumer-groups.sh --describe --group rjtest-cg2 --bootstrap-server localhost:9092

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                 HOST            CLIENT-ID
topic-aa        3          14              14              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-bb        3          9               9               0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-aa        2          12              12              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-bb        2          21              21              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-bb        1          43              43              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-aa        1          27              27              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-aa        0          30              30              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-aa        4          17              17              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-bb        4          15              15              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
topic-bb        0          12              12              0               KafkaExampleConsumer#5-0eb90651-423e-4742-8d5f-711a7c89e239 /127.0.0.1      KafkaExampleConsumer#5
```

Stopping one of the Kafka consumer in 'rjtest-cg1' correctly reshuffles the owned partition(s) toward the remaining running consumer(s).

## Running test scenario against Azure Event Hubs for Kafka

### Configuration

### Cluster Configuration

* Microsoft Azure Event Hubs (with "Enable Kafka" option)

2 Event Hub instances (map to Kafka topic concept) created: topic-aa and topic-bb (5 partitions each)

### Client Configuration

* Apache Kafka java client 1.1.0 library (kafka-clients maven artifact)

### Running scenario

#### Kafka consumers

Start Kafka consumers part of 'rjtest-cg1' consumer group:

```bash
rjtest-kafka-consumer.sh --id 0 --mode azure-eh -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 1 --mode azure-eh -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 2 --mode azure-eh -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 3 --mode azure-eh -g rjtest-cg1 -t topic-aa
rjtest-kafka-consumer.sh --id 4 --mode azure-eh -g rjtest-cg1 -t topic-aa
```

Start Kafka consumers part of 'rjtest-cg2' consumer group:

```bash
rjtest-kafka-consumer.sh --id 5 --mode azure-eh -g rjtest-cg2 -t topic-aa,topic-bb
```

#### Kafka producer

Send 100 messages to both topics:

```bash
rjtest-kafka-producer.sh --mode azure-eh -m 100 -t topic-aa,topic-bb
```