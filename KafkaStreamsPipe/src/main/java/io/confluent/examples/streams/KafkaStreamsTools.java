package io.confluent.examples.streams;

import io.confluent.common.utils.TestUtils;
import io.confluent.shaded.com.google.gson.Gson;
import io.confluent.shaded.com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Properties;

public class KafkaStreamsTools {
    private String bootstrapServers;
    private Properties streamsConfiguration;
    private StreamsBuilder builder;
    private Gson gson;

    public KafkaStreamsTools(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        streamsConfiguration = getStreamsConfiguration(bootstrapServers);
        gson = new GsonBuilder().create();

        // Define the processing topology of the Streams application.
        builder = new StreamsBuilder();
    }

    public void createMessagesCountStream(String inputTopic,
                                          String outputTopic,
                                          final KeyValueMapper<String, WikiMessage, String> groupByFunction) {
        Gson gson = new GsonBuilder().create();

        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<String, Long> wordCounts = textLines
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .count();

        // Write the `KTable<String, Long>` to the output topic.
        wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }

    public void createMessagesCountStream(String inputTopic,
                                          String outputTopic,
                                          final KeyValueMapper<String, WikiMessage, String> groupByFunction,
                                          final Predicate<String, WikiMessage> filterFunction) {
        Gson gson = new GsonBuilder().create();

        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<String, Long> wordCounts = textLines
                .filter((key, value) -> {
                    WikiMessage data = parseJsonToWikiMessage(value);
                    if (data != null)
                        return filterFunction.test(key, data);
                    else
                        return false;
                })
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .count();

        // Write the `KTable<String, Long>` to the output topic.
        wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }

    public void createMessagesCountStream(String inputTopic,
                                          String outputTopic,
                                          Duration windowDuration,
                                          final KeyValueMapper<String, WikiMessage, String> groupByFunction) {


        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<Windowed<String>, Long> wordCounts = textLines
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowDuration))
                .count();

        // Write the `KTable<String, Long>` to the output topic.
        wordCounts.toStream((windowedRegion, count) -> windowedRegion.toString())
                .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }

    public void createTopMessagesCountStream(String inputTopic,
                                             String outputTopic,
                                             final KeyValueMapper<String, WikiMessage, String> groupByFunction,
                                             final Predicate<String, WikiMessage> filterFunction) {
        Gson gson = new GsonBuilder().create();

        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<String, Long> wordCounts = textLines
                .filter((key, value) -> {
                    WikiMessage data = parseJsonToWikiMessage(value);
                    if (data != null)
                        return filterFunction.test(key, data);
                    else
                        return false;
                })
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .count();

        wordCounts.groupBy((s, aLong) -> KeyValue.pair("top", new StringEntry(s, aLong)),
                        Grouped.with(Serdes.String(), StringEntry.serde()))
                .aggregate(TopEntries::new, (s, entry, topEntries) -> {
                    topEntries.add(entry);
                    return topEntries;
                }, (s, entry, topEntries) -> {
                    topEntries.remove(entry);
                    return topEntries;
                }, Materialized.with(Serdes.String(), new TopEntriesSerDe()))
                .toStream()
                .to(outputTopic, Produced.with(Serdes.String(), new TopEntriesSerDe()));
    }

    public void createTopMessagesCountStream(String inputTopic,
                                             String outputTopic,
                                             final KeyValueMapper<String, WikiMessage, String> groupByFunction) {


        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<String, Long> wordCounts = textLines
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .count();

        // Write the `KTable<String, Long>` to the output topic.
        wordCounts.groupBy((s, aLong) -> KeyValue.pair("top", new StringEntry(s, aLong)),
                        Grouped.with(Serdes.String(), StringEntry.serde()))
                .aggregate(TopEntries::new, (s, entry, topEntries) -> {
                    topEntries.add(entry);
                    return topEntries;
                }, (s, entry, topEntries) -> {
                    topEntries.remove(entry);
                    return topEntries;
                }, Materialized.with(Serdes.String(), new TopEntriesSerDe()))
                .toStream()
                .to(outputTopic, Produced.with(Serdes.String(), new TopEntriesSerDe()));
    }

    public void createTopMessagesCountStreamWithTitle(String inputTopic,
                                             String outputTopic,
                                             final KeyValueMapper<String, WikiMessage, String> groupByFunction) {

        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<String, Long> wordCounts = textLines
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .count();

        // Write the `KTable<String, Long>` to the output topic.
        wordCounts.groupBy((s, aLong) -> KeyValue.pair("top", new StringEntry(s, aLong)),
                        Grouped.with(Serdes.String(), StringEntry.serde()))
                .aggregate(TopEntries::new, (s, entry, topEntries) -> {
                    topEntries.add(entry);
                    return topEntries;
                }, (s, entry, topEntries) -> {
                    topEntries.remove(entry);
                    return topEntries;
                }, Materialized.with(Serdes.String(), new TopEntriesSerDe()))
                .toStream()
                .to(outputTopic, Produced.with(Serdes.String(), new TopEntriesSerDe()));
    }

    public void createTopMessagesCountStream(String inputTopic,
                                             String outputTopic,
                                             Duration windowDuration,
                                             final KeyValueMapper<String, WikiMessage, String> groupByFunction) {


        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<Windowed<String>, Long> wordCounts = textLines
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowDuration))
                .count();

        // Write the `KTable<String, Long>` to the output topic.
        wordCounts.groupBy((s, aLong) -> KeyValue.pair("top", new StringEntry(s.key(), aLong)),
                        Grouped.with(Serdes.String(), StringEntry.serde()))
                .aggregate(TopEntries::new, (s, entry, topEntries) -> {
                    topEntries.add(entry);
                    return topEntries;
                }, (s, entry, topEntries) -> {
                    topEntries.remove(entry);
                    return topEntries;
                }, Materialized.with(Serdes.String(), new TopEntriesSerDe()))
                .toStream()
                .to(outputTopic, Produced.with(Serdes.String(), new TopEntriesSerDe()));
    }

    public void runStreams() {
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        // Always (and unconditionally) clean local state prior to starting the processing topology.
        // We opt for this unconditional call here because this will make it easier for you to play around with the example
        // when resetting the application for doing a re-run (via the Application Reset Tool,
        // https://docs.confluent.io/platform/current/streams/developer-guide/app-reset-tool.html).
        //
        // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
        // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
        // Thus in a production scenario you typically do not want to clean up always as we do here but rather only when it
        // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
        // See `ApplicationResetExample.java` for a production-like example.
        streams.cleanUp();

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private WikiMessage parseJsonToWikiMessage(String json) {
        try {
            return gson.fromJson(json, WikiMessage.class);
        } catch (Exception e) {
            System.out.println("Non serialized json accepted.");
            return null;
        }
    }

    /**
     * Configure the Streams application.
     * <p>
     * Various Kafka Streams related settings are defined here such as the location of the target Kafka cluster to use.
     * Additionally, you could also define Kafka Producer and Kafka Consumer settings when needed.
     *
     * @param bootstrapServers Kafka cluster address
     * @return Properties getStreamsConfiguration
     */
    private Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-streams-3");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "my-streams-3");
        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // Records should be flushed every 10 seconds. This is less than the default
        // in order to keep this example interactive.
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        // For illustrative purposes we disable record caches.
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // Use a temporary directory for storing state, which will be automatically removed after the test.
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());
        return streamsConfiguration;
    }
}
