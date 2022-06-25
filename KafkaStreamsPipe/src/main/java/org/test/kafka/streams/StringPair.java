package org.test.kafka.streams;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class StringPair implements Serializable {
    String left;
    String right;

    public StringPair(String string1, String string2) {
        this.left = string1;
        this.right = string2;
    }

    public static Serde<StringPair> serde() {
        return new Serde<StringPair>() {
            private Gson gson = new Gson();

            @Override
            public Serializer<StringPair> serializer() {
                return (s, entry) -> gson.toJson(entry).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Deserializer<StringPair> deserializer() {
                return (s, bytes) -> gson.fromJson(new String(bytes, StandardCharsets.UTF_8), StringPair.class);
            }
        };
    }
}
