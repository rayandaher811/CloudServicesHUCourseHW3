package org.bg.test;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + "9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "2");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
//        KafkaConsumer consumer = new KafkaConsumer(props);

        ConcurrentHashMap<String, TopicStatus> topicsToStatus = new ConcurrentHashMap<>();
        ArrayList<String> messages = new ArrayList<>();
        messages.add("page-creation-count");         // for que 1.a
        messages.add("page-update-count");           // for que 1.b
        messages.add("page-revert-action-count");    // for que 1.c
        messages.add("user-activities-count");       // for que 2
        messages.add("page-activities-count");       // for que 3
        messages.stream().forEach(s -> {
            topicsToStatus.put(s, new TopicStatus(s));
        });

        try {
            ExecutorService service = Executors.newFixedThreadPool(5);
            final BasicConsumeLoop<String, Long> basicConsumeLoop = new BasicConsumeLoop<>(BasicConsumeLoop.basicConsumerLoopConfig(), messages, topicsToStatus);
            service.execute(basicConsumeLoop);


            System.out.println("Hello user! this is our super statitstics system");
            System.out.println("Press 1 for total pages count");
            System.out.println("Press 2 for total pages changes count");
            System.out.println("Press 3 for total pages revert count");
            System.out.println("Press 4 for top 10 most active users");
            System.out.println("Press 5 for top 10 most active pages");
            System.out.println("Enter 99 to stop and exit!");
            Scanner in = new Scanner(System.in);
            int choose = in.nextInt();
            while (choose != 99) {
                switch (choose) {
                    case 1:
                        System.out.println("Number of pages created is: " + topicsToStatus.get("page-creation-count").getCounter());
                        break;
                    case 2:
                        System.out.println("Number of pages updated is: " + topicsToStatus.get("page-update-count").getCounter());
                        break;
                    case 3:
                        System.out.println("Number of pages reverted is: " + topicsToStatus.get("page-revert-action-count").getCounter());
                        break;
                    case 4:
                        System.out.println("10 of most active users: " + topicsToStatus.get("user-activities-count").getUserToValue());
                        break;
                    case 5:
                        System.out.println("10 of most active pages: " + topicsToStatus.get("page-activities-count").getUserToValue());
                        break;
                    default:
                        System.out.println("Unknown option.. please try again...");
                }
                System.out.println("Hello user! this is our super statitstics system");
                System.out.println("Press 1 for total pages count");
                System.out.println("Press 2 for total pages changes count");
                System.out.println("Press 3 for total pages revert count");
                System.out.println("Press 4 for top 10 most active users");
                System.out.println("Press 5 for top 10 most active pages");
                System.out.println("Enter 99 to stop and exit!");
                choose = in.nextInt();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    basicConsumeLoop.close();
                }
            }));

        } catch (Exception e) {
            System.exit(0);
        }


//        while (true) {
//            ConsumerRecords<String, Long> records = consumer.poll(1000);
//            for (ConsumerRecord<String, Long> record : records) {
//                if (record.topic().equals("page-creation-count") ||
//                record.topic().equals("page-update-count") ||
//                record.topic().equals("page-revert-action-count")) {
//                    if (topicsToStatus.containsKey(record.topic())) {
//                        topicsToStatus.get(record.topic()).setCounter(record.value());
//                    } else {
//                        System.out.println("BG error!!");
//                        System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
//                    }
//                } else if (record.topic().equals("user-activities-count") || record.topic().equals("page-activities-count")) {
//                    if (topicsToStatus.containsKey(record.topic())) {
//                        topicsToStatus.get(record.topic()).addResult(record.key(), record.value());
//                    } else {
//                        System.out.println("BG error!!");
//                        System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
//                    }
//                }
//            }
//        }

    }
}
