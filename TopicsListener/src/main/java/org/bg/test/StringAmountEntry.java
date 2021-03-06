package org.bg.test;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringAmountEntry implements Serializable {
    String string;
    long amount;

    public StringAmountEntry(String string, long amount) {
        this.string = string;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringAmountEntry that = (StringAmountEntry) o;
        return amount == that.amount && Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, amount);
    }

    public static Serde<StringAmountEntry> serde() {
        return new Serde<StringAmountEntry>() {
            private Gson gson = new Gson();

            @Override
            public Serializer<StringAmountEntry> serializer() {
                return (s, entry) -> gson.toJson(entry).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Deserializer<StringAmountEntry> deserializer() {
                return (s, bytes) -> gson.fromJson(new String(bytes, StandardCharsets.UTF_8), StringAmountEntry.class);
            }
        };
    }
}
