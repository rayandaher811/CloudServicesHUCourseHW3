package org.test.kafka.streams;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class TopEntriesSerDe implements Serde<TopEntries> {

    private final Gson gson = new Gson();

    @Override
    public Serializer<TopEntries> serializer() {
        return new TopEntriesSerializer();
    }

    @Override
    public Deserializer<TopEntries> deserializer() {
        return new TopEntriesDeserializer();
    }

    private class TopEntriesSerializer implements Serializer<TopEntries> {

        @Override
        public byte[] serialize(String s, TopEntries topEntries) {
            if (topEntries == null) {
                return new byte[0];
            }
            return gson.toJson(topEntries).getBytes(StandardCharsets.UTF_8);
        }
    }

    private class TopEntriesDeserializer implements Deserializer<TopEntries> {

        @Override
        public TopEntries deserialize(String s, byte[] bytes) {
            if (bytes.length == 0) {
                return new TopEntries();
            }
            return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), TopEntries.class);
        }
    }
}
