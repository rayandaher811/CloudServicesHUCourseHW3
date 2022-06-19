package io.confluent.examples.streams;

import io.confluent.shaded.com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringEntry implements Serializable {
    String string;
    long amount;

    public StringEntry(String string, long amount) {
        this.string = string;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringEntry that = (StringEntry) o;
        return amount == that.amount && Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, amount);
    }

    public static Serde<StringEntry> serde() {
        return new Serde<StringEntry>() {
            private Gson gson = new Gson();

            @Override
            public Serializer<StringEntry> serializer() {
                return (s, entry) -> gson.toJson(entry).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Deserializer<StringEntry> deserializer() {
                return (s, bytes) -> gson.fromJson(new String(bytes, StandardCharsets.UTF_8), StringEntry.class);
            }
        };
    }
}
