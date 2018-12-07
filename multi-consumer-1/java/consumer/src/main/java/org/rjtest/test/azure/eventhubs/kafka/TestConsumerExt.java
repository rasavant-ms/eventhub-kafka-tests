package org.rjtest.test.azure.eventhubs.kafka;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


/**
 * @author asaintsever
 *
 *usage: 
 *  help:   mvn exec:java -Dexec.mainClass="org.rjtest.test.azure.eventhubs.kafka.TestConsumerExt" -Dexec.args="-h"
 *  
 *  mvn exec:java -Dexec.mainClass="org.rjtest.test.azure.eventhubs.kafka.TestConsumerExt" -Dexec.args="--group=<consumer group> --id <consumer client id> -t=<topic name 1>,..,<topic name X>"
 */
public class TestConsumerExt {
    // Default values
    private static String[] TOPICS = {"topic-aa"};
    private static String CONSUMER_GROUP = "rjtest-cg1";
    private static int CONSUMER_CLIENT_ID = 0;
    private static boolean FROMBEGINNING = false;
    private static long POLL_TIMEOUT = 1000;
    private static String CONFIG_FILE = "src/main/resources/rjtest/rjtest.azure.eventhubs.consumer.config";
    private static Consumer<Long, String> consumer;
    
    private static CountDownLatch shutdownLatch = new CountDownLatch(1);
    

    public static void main(String... args) throws Exception {
        // Parse command line
        commandLine(args);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(consumer != null)
                    consumer.wakeup();  // raise a WakeupException from ongoing kafka blocking operations (e.g. poll)
                
                try {
                    shutdownLatch.await();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        try {
            consumer = createConsumer();
            
            // Subscribe to the topic(s).
            consumer.subscribe(Arrays.asList(TOPICS));
            
            System.out.println("Polling topics " + Arrays.asList(TOPICS));
            
            while(true) {
                final ConsumerRecords<Long, String> consumerRecords = consumer.poll(POLL_TIMEOUT);
                
                for(ConsumerRecord<Long, String> cr : consumerRecords) {
                    System.out.printf("[%s - %s] Consumer Record:(key:%d, value:%s, partition:%d, offset:%d)\n", cr.topic(), CONSUMER_GROUP, cr.key(), cr.value(), cr.partition(), cr.offset());
                }
                
                consumer.commitAsync();
            }
        } catch (WakeupException e) {
         // Consumer will be closed
            System.out.println("Exiting...");
        } catch (CommitFailedException e) {
            System.out.println("CommitFailedException: " + e);
        }
        finally {
            System.out.println("Closing...");
            
            if(consumer != null)
                consumer.close();
            
            shutdownLatch.countDown();
            
            System.out.println("... Closed");
        }
    }
    
    private static void commandLine(String... args) throws ParseException {
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("display help")
                .build();
        
        Option mode = Option.builder()
                .longOpt("mode")
                .argName("name")
                .hasArg()
                .desc("native (for true kafka broker), azure-eh (for azure event hubs). Default to native.")
                .build();
        
        Option topics = Option.builder("t")
                .longOpt("topics")
                .argName("name")
                .hasArg()
                .desc("subscribe to kafka topic(s). Names separated by ','")
                .build();
        
        Option id = Option.builder("i")
                .longOpt("id")
                .argName("num")
                .hasArg()
                .desc("unique consumer id")
                .build();
        
        Option group = Option.builder("g")
                .longOpt("group")
                .argName("name")
                .hasArg()
                .desc("consumer group name")
                .build();
        
        Option frombeginning = Option.builder()
                .longOpt("from-beginning")
                .desc("offset set to beginning of topic")
                .build();
        
        Option polltimeout = Option.builder()
                .longOpt("poll-timeout")
                .argName("ms")
                .hasArg()
                .desc("time, in milliseconds, spent waiting in poll if data is not available in the buffer. If 0, returns immediately. Default to 1000 ms.")
                .build();
        
        Options options = new Options();
        options.addOption(help);
        options.addOption(mode);
        options.addOption(frombeginning);
        options.addOption(topics);
        options.addOption(id);
        options.addOption(group);
        options.addOption(polltimeout);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        
        if(cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("TestProducerExt", options, true);
            System.exit(0);
        }
        
        if(cmd.hasOption("mode")) {
            if("native".equals(cmd.getOptionValue("mode").toLowerCase()))
                CONFIG_FILE = "src/main/resources/rjtest/rjtest.native.consumer.config";
            
            if("azure-eh".equals(cmd.getOptionValue("mode").toLowerCase()))
                CONFIG_FILE = "src/main/resources/rjtest/rjtest.azure.eventhubs.consumer.config";
        }
        
        if(cmd.hasOption("from-beginning")) {
            FROMBEGINNING = true;
        }
        
        if(cmd.hasOption("poll-timeout")) {
            POLL_TIMEOUT = Long.parseLong(cmd.getOptionValue("poll-timeout"));
        }
        
        if(cmd.hasOption("i")) {
            CONSUMER_CLIENT_ID = Integer.parseInt(cmd.getOptionValue("i"));
        }
        
        if(cmd.hasOption("g")) {
            CONSUMER_GROUP = cmd.getOptionValue("g");
        }
        
        if(cmd.hasOption("t")) {
            TOPICS = cmd.getOptionValue("t").split(",");
        }
    }
    
    private static Consumer<Long, String> createConsumer() {
        try {
            final Properties properties = new Properties();
            properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "KafkaExampleConsumer#" + CONSUMER_CLIENT_ID);
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
            
            if(FROMBEGINNING)
                properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            //Get remaining properties from config file
            properties.load(new FileReader(CONFIG_FILE));

            // Create the consumer using properties.
            final Consumer<Long, String> consumer = new KafkaConsumer<>(properties);
            return consumer;
        } catch (FileNotFoundException e){
            System.out.println("FileNoteFoundException: " + e);
            System.exit(1);
            return null;        //unreachable
        } catch (IOException e){
            System.out.println("IOException: " + e);
            System.exit(1);
            return null;        //unreachable
        }
    }
}
