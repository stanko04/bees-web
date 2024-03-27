package sk.fiit.bp.BeesWeb.model.geoJson;

import lombok.Data;

import java.util.List;

@Data
public class GeoJsonFeatureCollection {
    private String type;
    private List<GeoJsonFeature> features;
}

