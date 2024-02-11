package sk.fiit.bp.DataGenerator.model.mapDevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Attribute {
    @JsonAlias("lastUpdateTs")
    private long lastUpdateTs;

    @JsonAlias("key")
    private String key;

    @JsonAlias("value")
    private Object value;
}
