package org.bg.test;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TopicStatus {
    private String query;
    private Long counter;
    private static final Gson GSON = new Gson();

    private HashMap<String, Long> keyValueMapper;

    private HashMap<String, List<StringAmountEntry>> languageMapper;

    public TopicStatus(String query) {
        this.query = query;
        this.counter = Long.valueOf(0);
        this.keyValueMapper = new HashMap<>();
        this.languageMapper = new HashMap<>();
    }

    public void setCounter(String receivedTopicName, Long counter) {
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

    public void addLanguageResult(String key, String value) {
        TopEntries consumedTopEntries = GSON.fromJson(value, TopEntries.class);
        if (consumedTopEntries != null) {
            synchronized (keyValueMapper) {
                if (languageMapper.containsKey(consumedTopEntries.title)) {
                    languageMapper.remove(consumedTopEntries.title);
                }
                // TODO: think about it.. it means we don't update data.. we re-write it..
                languageMapper.put(consumedTopEntries.title, consumedTopEntries.getSortedEntries());
            }
        }
    }

    public void addLanguageResult(String language, Long value) {
        keyValueMapper.put(language, value);
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

    public HashMap<String, List<StringAmountEntry>> getLanguageMapper() {
        return languageMapper;
    }

    public String getLanguageMapperAsString() {
        AtomicReference<String> res = new AtomicReference<>("");
        languageMapper.forEach((s, stringAmountEntries) -> {
            List<String> pagesList = stringAmountEntries.stream().map(stringAmountEntry -> stringAmountEntry.string).collect(Collectors.toList());
            res.getAndSet(res.get() + "Language: " + s + ", pages: " + pagesList + "\n");
        });
        return res.get();
    }
}
