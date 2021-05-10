package app.cacheservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CachePayLoad implements Serializable, Comparable<CachePayLoad> {

    String key;
    long timestamp;
    String payload;

    @Override
    public int compareTo(CachePayLoad o) {
        return key.compareTo(o.key) + Long.compare(timestamp, o.timestamp);
    }
}
