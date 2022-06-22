package org.bg.test;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class TopicStatus {
    private String query;
    private Long counter;
    private static final Gson GSON = new Gson();

    private HashMap<String, Long> keyValueMapper;


    public TopicStatus(String query) {
        this.query = query;
        this.counter = Long.valueOf(0);
        this.keyValueMapper = new HashMap<>();
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public void addResult(String key, String value) {
        TopEntries consumedTopEntries = GSON.fromJson(value, TopEntries.class);
        synchronized (keyValueMapper) {
            keyValueMapper.clear();
            consumedTopEntries.getSortedEntries().forEach(stringAmountEntry -> {
                keyValueMapper.put(stringAmountEntry.string, stringAmountEntry.amount);
            });
        }
    }

    public Long getCounter() {
        return counter;
    }

    public HashMap<String, Long> getKeyValueMapper() {
        return keyValueMapper;
    }

    @Override
    public String toString() {
        AtomicReference<String> valueToPrint = new AtomicReference<>("Query Status {\n" +
                "query='" + query + '\'');
        synchronized (keyValueMapper) {
            keyValueMapper.forEach((s, aLong) -> {
                valueToPrint.getAndSet(valueToPrint.get() + "Key: " + s + ", value: " + aLong + "\n");
            });
        }
        valueToPrint.getAndSet(valueToPrint.get() + "}");

        return valueToPrint.get();
    }
}
