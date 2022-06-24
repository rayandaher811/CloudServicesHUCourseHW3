package org.bg.test;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

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
        StringBuilder stringBuilder = new StringBuilder();
        synchronized (keyValueMapper) {
            keyValueMapper.forEach((s, aLong) -> stringBuilder.append(s + ": " + aLong + "\n"));
        }
        stringBuilder.append("\n\n");

        return stringBuilder.toString();
    }

    public String getLanguageMapperAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        languageMapper.forEach((s, languageAmountEntry) -> {
            stringBuilder.append("Language: " + s + "\n");
            languageAmountEntry.forEach(stringAmountEntry -> stringBuilder.append(stringAmountEntry.string + ": " + stringAmountEntry.amount + "\n"));
            stringBuilder.append("\n\n");
        });
        stringBuilder.append("\n\n");

        return stringBuilder.toString();
    }
}
