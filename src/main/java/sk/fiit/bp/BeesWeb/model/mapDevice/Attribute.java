package sk.fiit.bp.BeesWeb.model.mapDevice;

import com.fasterxml.jackson.annotation.JsonAlias;
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
