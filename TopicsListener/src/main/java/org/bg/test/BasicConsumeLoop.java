package org.bg.test;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class BasicConsumeLoop<K extends Serializable, V extends Serializable> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BasicConsumeLoop.class);

    private final KafkaConsumer<K, V> consumer;
    private final List<String> topics;
    private final String clientId;

    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private ConcurrentHashMap<String, TopicStatus> topicsToStatus;

    public BasicConsumeLoop(Properties configs, List<String> topics, ConcurrentHashMap<String, TopicStatus> topicsToStatus) {
        this.clientId = configs.getProperty(ConsumerConfig.CLIENT_ID_CONFIG);
        this.topics = topics;
        this.consumer = new KafkaConsumer<>(configs);
        this.topicsToStatus = topicsToStatus;
    }

    @Override
    public void run() {

        try {
            logger.info("Starting the Consumer : {}", clientId);

            ConsumerRebalanceListener listener = new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    logger.info("C : {}, Revoked partitionns : {}", (Object) clientId, partitions);
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    logger.info("C : {}, Assigned partitions : {}", clientId, partitions);
                }
            };
            consumer.subscribe(topics, listener);

            logger.info("C : {}, Started to process records", clientId);
            while (true) {
                ConsumerRecords<K, V> records = consumer.poll(Long.MAX_VALUE);

                if (records.isEmpty()) {
                    logger.info("C : {}, Found no records", clientId);
                    continue;
                }

                logger.info("C : {} Total No. of records received : {}", clientId, records.count());
                for (ConsumerRecord<K, V> record : records) {
                    logger.info("C : {}, Record received partition : {}, key : {}, value : {}, offset : {}",
                            clientId, record.partition(), record.key(), record.value(), record.offset());
                    if (
                            record.topic().equals("page-creation-count") ||
                                    record.topic().equals("page-revert-action-count") ||
                                    record.topic().equals("page-update-count") ||
                                    record.topic().equals("page-creation-hourly-count") ||
                                    record.topic().equals("page-revert-action-hourly-count") ||
                                    record.topic().equals("page-update-hourly-count") ||
                                    record.topic().equals("page-creation-daily-count") ||
                                    record.topic().equals("page-revert-action-daily-count") ||
                                    record.topic().equals("page-update-daily-count") ||
                                    record.topic().equals("page-creation-weekly-count") ||
                                    record.topic().equals("page-revert-action-weekly-count") ||
                                    record.topic().equals("page-update-weekly-count") ||
                                    record.topic().equals("page-creation-monthly-count") ||
                                    record.topic().equals("page-revert-action-monthly-count") ||
                                    record.topic().equals("page-update-monthly-count")
                    ) {
                        if (topicsToStatus.containsKey(record.topic())) {
                            topicsToStatus.get(record.topic()).setCounter((Long) record.value());
                        } else {
                            System.out.println("BG error!!");
                            System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
                        }
                    } else if (
                            record.topic().equals("user-activities-count") ||
                                record.topic().equals("page-activities-count") ||
                                record.topic().equals("page-activities-hourly-count")  ||
                                record.topic().equals("user-activities-hourly-count") ||
                                record.topic().equals("page-activities-daily-count") ||
                                record.topic().equals("user-activities-daily-count") ||
                                record.topic().equals("page-activities-weekly-count") ||
                                record.topic().equals("user-activities-weekly-count") ||
                                record.topic().equals("page-activities-monthly-count") ||
                                record.topic().equals("user-activities-monthly-count")
                    ) {
                        if (topicsToStatus.containsKey(record.topic())) {
                            topicsToStatus.get(record.topic()).addResult(record.topic(), (String) record.value());
                        } else {
                            System.out.println("BG error!!");
                            System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
                        }
                    }
                    sleep(50);
                }


                // `enable.auto.commit` set to true. coordinator automatically commits the offsets
                // returned on the last poll(long) for all the subscribed list of topics and partitions
            }
        } catch (
                WakeupException e) {
            // Ignore, we're closing
        } finally {
            consumer.close();
            shutdownLatch.countDown();
            logger.info("C : {}, consumer exited", clientId);
        }

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }

    public void close() {
        try {
            consumer.wakeup();
            shutdownLatch.await();
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }

    public static Properties stringStringConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + "9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "2");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    public static Properties stringLongConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + "9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "5");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        return props;
    }
}
