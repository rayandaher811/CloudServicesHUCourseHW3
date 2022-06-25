package org.test.kafka.streams;

import com.google.gson.Gson;
import io.confluent.common.utils.TestUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Properties;
import java.util.function.Function;

public class KafkaStreamsTools {
    private static final Gson GSON = new Gson();
    private final Properties streamsConfiguration;
    private final StreamsBuilder builder;

    public KafkaStreamsTools(String bootstrapServers) {
        streamsConfiguration = getStreamsConfiguration(bootstrapServers);
        builder = new StreamsBuilder();
    }

    public void createMessagesCountStream(String inputTopic,
                                          String outputTopic,
                                          final KeyValueMapper<String, WikiMessage, String> groupByFunction) {
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

        wordCounts.toStream((windowedRegion, count) -> windowedRegion.toString())
                .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }

    public void createTopMessagesCountStream(String inputTopic,
                                             String outputTopic,
                                             final KeyValueMapper<String, WikiMessage, String> groupByFunction,
                                             final Predicate<String, WikiMessage> filterFunction) {
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

        aggregateToTopEntries(wordCounts, outputTopic);
    }

    public void createTopMessagesCountStream(String inputTopic,
                                             String outputTopic,
                                             final KeyValueMapper<String, WikiMessage, String> groupByFunction) {


        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<String, Long> wordCounts = textLines
                .groupBy((key, value) -> groupByFunction.apply(key, parseJsonToWikiMessage(value)))
                .count();

        aggregateToTopEntries(wordCounts, outputTopic);
    }

    private void aggregateToTopEntries(KTable<String, Long> wordCounts, String outputTopic) {
        wordCounts.groupBy((s, aLong) -> KeyValue.pair("top", new StringAmountEntry(s, aLong)),
                        Grouped.with(Serdes.String(), StringAmountEntry.serde()))
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
        wordCounts.groupBy((s, aLong) -> KeyValue.pair("top", new StringAmountEntry(s.key(), aLong)),
                        Grouped.with(Serdes.String(), StringAmountEntry.serde()))
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
                                                      Function<WikiMessage, String> titleExtractor,
                                                      Function<WikiMessage, String> valueExtractor) {

        final KStream<String, String> textLines = builder.stream(inputTopic);

        final KTable<StringPair, Long> wordCounts = textLines
                .groupBy((key, value) -> new StringPair(titleExtractor.apply(parseJsonToWikiMessage(value)), valueExtractor.apply(parseJsonToWikiMessage(value))),
                        Grouped.keySerde(StringPair.serde()))
                .count();

        wordCounts.groupBy((countedPair, aLong) -> KeyValue.pair(countedPair.left, new StringAmountEntry(countedPair.right, aLong)),
                        Grouped.with(Serdes.String(), StringAmountEntry.serde()))
                .aggregate(TopEntries::new, (s, entry, topEntries) -> {
                    topEntries.title = s;
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

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private WikiMessage parseJsonToWikiMessage(String json) {
        try {
            return GSON.fromJson(json, WikiMessage.class);
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
