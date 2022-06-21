package org.bg.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TopicStatus {
    private String query;
    private Long counter;

    private HashMap<String, Long> userToValue;


    public TopicStatus(String query) {
        this.query = query;
        this.counter = Long.valueOf(0);
        this.userToValue = new HashMap<>();
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public void addResult(String key, Long value) {
        if (userToValue.containsKey(key) || userToValue.size() < 10) {
            userToValue.put(key, value);
        } else {
            Map.Entry<String, Long> min = Collections.min(userToValue.entrySet(),
                    Comparator.comparing(Map.Entry::getValue));
            if (value > min.getValue()) {
                userToValue.remove(min.getKey(), min.getValue());
                userToValue.put(key, value);
            }
        }
    }

    public Long getCounter() {
        return counter;
    }

    public HashMap<String, Long> getUserToValue() {
        return userToValue;
    }
}
