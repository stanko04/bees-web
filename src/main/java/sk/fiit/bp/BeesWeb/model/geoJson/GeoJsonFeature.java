package sk.fiit.bp.BeesWeb.model.geoJson;

import lombok.Data;

import java.util.List;

@Data
public class GeoJsonFeature {
    private String type;
    private GeoJsonProperties properties;
    private GeoJsonGeometry geometry;

    public static GeoJsonFeature createGeoJsonFeature(String name, double longitude, double latitude,
                                                      String owner, String dashboard) {
        GeoJsonFeature feature = new GeoJsonFeature();
        feature.setType("Feature");

        GeoJsonProperties properties = new GeoJsonProperties();
        if(dashboard.equals("unknown")) {
            properties.setDescription("<strong>" + name + "</strong>" +
                    "<p><strong>Owner (beekeeper): </strong>" + owner + "<p>");
        } else {
            properties.setDescription("<strong>" + name + "</strong>" +
                    "<p><strong>Owner (beekeeper): </strong>" + owner + "<p>" +
                    "<p><a href=\"" + dashboard + "\" target=\"_blank\" title=\"Go to dashboard\">Display dashboard</a></p>");
        }

        properties.setIcon("embassy");

        feature.setProperties(properties);

        GeoJsonGeometry geometry = new GeoJsonGeometry();
        geometry.setType("Point");
        geometry.setCoordinates(List.of(longitude, latitude));

        feature.setGeometry(geometry);

        return feature;
    }
}
