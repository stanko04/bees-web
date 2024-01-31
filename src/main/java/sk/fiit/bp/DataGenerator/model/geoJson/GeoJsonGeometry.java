package sk.fiit.bp.DataGenerator.model.geoJson;

import lombok.Data;

import java.util.List;

@Data
public class GeoJsonGeometry {
    private String type;
    private List<Double> coordinates;

}
