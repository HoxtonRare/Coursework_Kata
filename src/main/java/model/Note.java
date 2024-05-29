package model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.Column;
import javax.persistence.Id;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class Note {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final AtomicLong generator = new AtomicLong(0);
    @Id
    private Long id;
    @Column
    private String text;
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
