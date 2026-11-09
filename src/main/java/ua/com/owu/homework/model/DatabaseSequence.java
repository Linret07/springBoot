package ua.com.owu.homework.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("database_sequences")
public class DatabaseSequence {
    @Id
    private String id;
    private long value;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }
}
