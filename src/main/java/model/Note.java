package model;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.Column;
import javax.persistence.Id;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Note {
    private static final AtomicLong generator = new AtomicLong(0);
    @Getter
    @Id
    private Long id;
    @Getter
    @Setter
    @Column
    private String text;
    @Getter
    @Setter
    @Column
    private Set<String> labels;

    public Note() {
    }

    public Note(String text, Set<String> labels) {
        id = generator.incrementAndGet();
        this.text = text;
        this.labels = labels;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Note && id.equals(((Note) obj).id);
    }

    @Override
    public String toString() {
        String labelsString = String.join(";", labels);
        return "\n" + id + "#" + text + "\n" + labelsString + "\n ============================";
    }
}
