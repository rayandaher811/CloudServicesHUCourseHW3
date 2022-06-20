package io.confluent.examples.streams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopEntries implements Serializable {
    private final List<StringEntry> sortedEntries;
    public String title;

    public TopEntries() {
        sortedEntries = new ArrayList<>();
        title = "";
    }

    public TopEntries(String title) {
        sortedEntries = new ArrayList<>();
        this.title = title;
    }

    public void add(StringEntry entry) {
        sortedEntries.add(entry);
        sortedEntries.sort((o1, o2) -> (int) (o2.amount - o1.amount));
        if (sortedEntries.size() > 5) {
            sortedEntries.remove(sortedEntries.remove(4));
        }
    }

    public void remove(StringEntry entry) {
        sortedEntries.remove(entry);
    }
}
