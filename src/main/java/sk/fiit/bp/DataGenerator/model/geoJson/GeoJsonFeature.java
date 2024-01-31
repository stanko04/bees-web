package sk.fiit.bp.DataGenerator.model.geoJson;

import lombok.Data;

import java.util.List;

@Data
public class GeoJsonFeature {
    private String type;
    private GeoJsonProperties properties;
    private GeoJsonGeometry geometry;

    public static GeoJsonFeature createGeoJsonFeature(String description, double lon, double lat, String icon) {
        GeoJsonFeature feature = new GeoJsonFeature();
        feature.setType("Feature");

        GeoJsonProperties properties = new GeoJsonProperties();
        properties.setDescription("<strong>" + description + "</strong>" + "<p><a href=\"\" target=\"_blank\" title=\"Zobraziť dashboard\">Zobraziť dashboard</a></p>");
        properties.setIcon(icon);

        feature.setProperties(properties);

        GeoJsonGeometry geometry = new GeoJsonGeometry();
        geometry.setType("Point");
        geometry.setCoordinates(List.of(lon, lat));

        feature.setGeometry(geometry);

        return feature;
    }
}
