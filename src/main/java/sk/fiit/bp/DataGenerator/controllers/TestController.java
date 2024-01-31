package sk.fiit.bp.DataGenerator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sk.fiit.bp.DataGenerator.model.geoJson.GeoJsonFeature;
import sk.fiit.bp.DataGenerator.model.geoJson.GeoJsonFeatureCollection;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {

    @GetMapping("/home")
    public String home(Model model) {
        String appName = "Toto je moja aplikacia";
        int number = 1234;
        String string = "string";
        model.addAttribute("number", number);
        model.addAttribute("string", string);
        model.addAttribute("appName", appName);
        return "home";
    }

    @GetMapping("/map")
    public String map(Model model) {
        // Generate GeoJSON data with multiple markers
        GeoJsonFeatureCollection featureCollection = new GeoJsonFeatureCollection();
        featureCollection.setType("FeatureCollection");

        List<GeoJsonFeature> features = new ArrayList<>();

        // Add the first marker
        GeoJsonFeature feature1 = GeoJsonFeature.createGeoJsonFeature("Včelí úľ č. 155", 17.58723, 48.37741, "embassy");
        features.add(feature1);

        // Add more markers as needed ,
         GeoJsonFeature feature2 = GeoJsonFeature.createGeoJsonFeature("Včelí úľ č. 188", 18.076376, 48.306141, "embassy");
         features.add(feature2);

        featureCollection.setFeatures(features);

        model.addAttribute("geoJsonData", featureCollection);
        return "map";
    }


}
