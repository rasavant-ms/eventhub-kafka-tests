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

## Running test scenario against Azure Event Hubs for Kafka

### Configuration

### Cluster Configuration

* Microsoft Azure Event Hubs (with "Enable Kafka" option)

2 Event Hub instances (map to Kafka topic concept) created: topic-aa and topic-bb (5 partitions each)

### Client Configuration

* Apache Kafka java client 1.1.0 library (kafka-clients maven artifact)

### Running scenario

Configure settings using [this document](java/README.md) before running the scenario

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
mvn exec:java -Dexec.mainClass="TestProducer"
```
