package sk.fiit.bp.BeesWeb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.fiit.bp.BeesWeb.model.database.Device;
import sk.fiit.bp.BeesWeb.model.geoJson.GeoJsonFeature;
import sk.fiit.bp.BeesWeb.model.geoJson.GeoJsonFeatureCollection;
import sk.fiit.bp.BeesWeb.repository.DeviceRepository;

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

        List<Device> devices = deviceRepository.findAll();
        List<GeoJsonFeature> features = getGeoJsonFeatures(devices);


        featureCollection.setFeatures(features);

        model.addAttribute("geoJsonData", featureCollection);

        return "index";
    }

    @GetMapping("/list")
    public String getMap(Model model) {
        // Generate GeoJSON data with multiple markers
        GeoJsonFeatureCollection featureCollection = new GeoJsonFeatureCollection();
        featureCollection.setType("FeatureCollection");

        List<Device> devices = deviceRepository.findAll();

        List<GeoJsonFeature> features = getGeoJsonFeatures(devices);

        featureCollection.setFeatures(features);

        model.addAttribute("devices", devices);
        model.addAttribute("geoJsonData", featureCollection);

        return "list";
    }

    @GetMapping("about-us")
    public String getAboutUs() {
        return "contact";
    }

    @GetMapping("about-project")
    public String getAboutProject() { return "about-project"; }

    @GetMapping("/test")
    public String getTest() {
        return "test";
    }

    public List<GeoJsonFeature> getGeoJsonFeatures(List<Device> devices) {
        List<GeoJsonFeature> features = new ArrayList<>();

        if(devices.size() > 0) {
            for(Device device: devices) {
                if(device.getLatitude() != null && device.getLongitude() != null) {
                    GeoJsonFeature feature;
                    if(device.getDashboardLink() != null) {
                        feature = GeoJsonFeature.createGeoJsonFeature(
                                device.getName(), device.getLongitude(), device.getLatitude(),
                                device.getOwner(), device.getDashboardLink());
                        device.setDescription(feature.getProperties().getDescription());
                    } else {
                        feature = GeoJsonFeature.createGeoJsonFeature(
                                device.getName(), device.getLongitude(), device.getLatitude(),
                                device.getOwner(), "unknown");
                        device.setDescription(feature.getProperties().getDescription());
                    }
                    features.add(feature);
                }
            }
        }

        return features;
    }

}
