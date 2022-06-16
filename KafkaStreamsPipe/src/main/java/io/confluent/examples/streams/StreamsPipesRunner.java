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
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.time.LocalDate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class StreamsPipesRunner {
  final static KafkaStreamsTools streams = new KafkaStreamsTools("localhost:9092");

  final static String pageUpdateTopic = "page-update";
  final static String pageCreationTopic = "page-creation";
  final static String pageRevertActionTopic = "page-revert-action";
  final static String pageEventTopic="page-event";

  public static void main(final String[] args) {
    // Creating permanent count streams
    CreatePermanentCountStreams();

    // Creating windowed count streams
    CreateWindowedCountStreams(Duration.ofHours(1), "hourly");
    CreateWindowedCountStreams(Duration.ofDays(1), "daily");
    CreateWindowedCountStreams(Duration.ofDays(7), "weekly");
    CreateWindowedCountStreams(Duration.ofDays(30), "monthly");

    // Creating bots and none bots count streams
    CreatePermanentFilteredCountStreams("bots", (key, wikiMessage)-> wikiMessage.bot);
    CreatePermanentFilteredCountStreams("none-bots", (key, wikiMessage)-> !wikiMessage.bot);

    CreatePermanentByLanguageCountStreams("language");
    streams.runStreams();
  }

  private static void CreatePermanentFilteredCountStreams(String kafkaTopicsSuffix, final Predicate<String, WikiMessage> filterFunction) {
    // Windowed count topics
    final String pageUpdateCountTopic = "page-update-"+kafkaTopicsSuffix+"-count";
    final String pageCreationCountTopic = "page-creation-"+kafkaTopicsSuffix+"-count";
    final String pageRevertActionCountTopic = "page-revert-action-"+kafkaTopicsSuffix+"-hour-count";
    final String userActivitiesCountTopic = "user-activities-"+kafkaTopicsSuffix+"-count";
    final String pageActivitiesCountTopic = "page-activities-"+kafkaTopicsSuffix+"-count";

    // Windowed count streams
    streams.createMessagesCountStream(pageUpdateTopic, pageUpdateCountTopic, (key,value)->"1", filterFunction);
    streams.createMessagesCountStream(pageCreationTopic, pageCreationCountTopic, (key,value)->"1", filterFunction);
    streams.createMessagesCountStream(pageRevertActionTopic, pageRevertActionCountTopic, (key,value)->"1", filterFunction);
    streams.createMessagesCountStream(pageEventTopic,userActivitiesCountTopic, (key,value)-> value.user, filterFunction);
    streams.createMessagesCountStream(pageEventTopic, pageActivitiesCountTopic, (key,value)->value.uri, filterFunction);
  }

  private static void CreateWindowedCountStreams(Duration windowDuration, String kafkaTopicsSuffix) {
    // Windowed count topics
    final String pageUpdateCountTopic = "page-update-"+kafkaTopicsSuffix+"-count";
    final String pageCreationCountTopic = "page-creation-"+kafkaTopicsSuffix+"-count";
    final String pageRevertActionCountTopic = "page-revert-action-"+kafkaTopicsSuffix+"-count";
    final String userActivitiesCountTopic = "user-activities-"+kafkaTopicsSuffix+"-count";
    final String pageActivitiesCountTopic = "page-activities-"+kafkaTopicsSuffix+"-count";

    // Windowed count streams
    streams.createMessagesCountStream(pageUpdateTopic, pageUpdateCountTopic,windowDuration, (key,value)->"1");
    streams.createMessagesCountStream(pageCreationTopic, pageCreationCountTopic, windowDuration, (key,value)->"1");
    streams.createMessagesCountStream(pageRevertActionTopic, pageRevertActionCountTopic, windowDuration, (key,value)->"1");
    streams.createMessagesCountStream(pageEventTopic,userActivitiesCountTopic, windowDuration, (key,value)-> value.user);
    streams.createMessagesCountStream(pageEventTopic, pageActivitiesCountTopic, windowDuration, (key,value)->value.uri);
  }

  private static void CreatePermanentCountStreams() {
    // Permanent count topics
    final String pageUpdateCountTopic = "page-update-count";
    final String pageCreationCountTopic = "page-creation-count";
    final String pageRevertActionCountTopic = "page-revert-action-count";
    final String userActivitiesCountTopic = "user-activities-count";
    final String pageActivitiesCountTopic = "page-activities-count";

    // Permanent count streams
    streams.createMessagesCountStream(pageUpdateTopic, pageUpdateCountTopic, (key,value)->"1");
    streams.createMessagesCountStream(pageCreationTopic, pageCreationCountTopic, (key,value)->"1");
    streams.createMessagesCountStream(pageRevertActionTopic, pageRevertActionCountTopic, (key,value)->"1");
    streams.createMessagesCountStream(pageEventTopic,userActivitiesCountTopic, (key,value)-> value.user);
    streams.createMessagesCountStream(pageEventTopic, pageActivitiesCountTopic, (key,value)->value.uri);
  }

  private static void CreatePermanentByLanguageCountStreams(String kafkaTopicsSuffix) {
    // Permanent count topics
    final String pageUpdateCountTopic = "page-update-"+kafkaTopicsSuffix +"-count";
    final String pageCreationCountTopic = "page-creation-"+kafkaTopicsSuffix +"-count";
    final String pageRevertActionCountTopic = "page-revert-action-"+kafkaTopicsSuffix +"-count";
    final String userActivitiesCountTopic = "user-activities-"+kafkaTopicsSuffix +"-count";
    final String pageActivitiesCountTopic = "page-activities-"+kafkaTopicsSuffix +"-count";

    // Permanent count streams
    streams.createMessagesCountStream(pageUpdateTopic, pageUpdateCountTopic, (key,value)->value.language);
    streams.createMessagesCountStream(pageCreationTopic, pageCreationCountTopic, (key,value)->value.language);
    streams.createMessagesCountStream(pageRevertActionTopic, pageRevertActionCountTopic, (key,value)->value.language);
    streams.createMessagesCountStream(pageEventTopic,userActivitiesCountTopic, (key,value)-> value.language + "___" + value.user);
    streams.createMessagesCountStream(pageEventTopic, pageActivitiesCountTopic, (key,value)->value.language + "___" + value.uri);
  }

}
