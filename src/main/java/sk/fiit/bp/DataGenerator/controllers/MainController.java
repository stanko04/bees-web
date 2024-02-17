package sk.fiit.bp.DataGenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.fiit.bp.DataGenerator.model.database.Device;
import sk.fiit.bp.DataGenerator.model.geoJson.GeoJsonFeature;
import sk.fiit.bp.DataGenerator.model.geoJson.GeoJsonFeatureCollection;
import sk.fiit.bp.DataGenerator.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/")
    public String home(Model model) {

        GeoJsonFeatureCollection featureCollection = new GeoJsonFeatureCollection();
        featureCollection.setType("FeatureCollection");

        List<GeoJsonFeature> features = getGeoJsonFeatures();


        featureCollection.setFeatures(features);

        model.addAttribute("geoJsonData", featureCollection);

        return "home";
    }

    @GetMapping("/list")
    public String getMap(Model model) {
        // Generate GeoJSON data with multiple markers
        GeoJsonFeatureCollection featureCollection = new GeoJsonFeatureCollection();
        featureCollection.setType("FeatureCollection");

        List<GeoJsonFeature> features = getGeoJsonFeatures();


        featureCollection.setFeatures(features);

        List<Device> devices = deviceRepository.findAll();

        model.addAttribute("devices", devices);
        model.addAttribute("geoJsonData", featureCollection);

        return "list";
    }

    @GetMapping("about-us")
    public String getAboutUs() {
        return "contact";
    }

    @GetMapping("/test")
    public String getTest() {
        return "test";
    }

    public List<GeoJsonFeature> getGeoJsonFeatures() {
        List<GeoJsonFeature> features = new ArrayList<>();
        List<Device> devices = deviceRepository.findAll();

        if(devices.size() > 0) {
            for(Device device: devices) {
                if(device.getLatitude() != null && device.getLongitude() != null) {
                    GeoJsonFeature feature;
                    if(device.getDashboardLink() != null) {
                        feature = GeoJsonFeature.createGeoJsonFeature(
                                device.getName(), device.getLongitude(), device.getLatitude(),
                                device.getOwner(), device.getDashboardLink());
                    } else {
                        feature = GeoJsonFeature.createGeoJsonFeature(
                                device.getName(), device.getLongitude(), device.getLatitude(),
                                device.getOwner(), "unknown");
                    }
                    features.add(feature);
                }
            }
        }

        return features;
    }

}
