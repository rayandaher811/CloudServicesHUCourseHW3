/*
 * Copyright Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.examples.streams;

import io.confluent.common.utils.TestUtils;
import io.confluent.shaded.com.google.gson.Gson;
import io.confluent.shaded.com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;

import java.time.Duration;
import java.time.LocalDate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class TopicMessagesCounter {

  public static void main(final String[] args) {
    final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
    final String inputTopic = args.length > 1 ? args[1] : "page-update";
    final String outputTopic = args.length > 2 ? args[2] : "page-revert-action";

    KafkaStreamsTools streams = new KafkaStreamsTools(bootstrapServers, inputTopic, outputTopic);

    streams.createMessagesCountStream();

    streams.runStreams();
  }

}
