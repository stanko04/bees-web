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
public class HomeController {

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/")
    public String home(Model model) {
        String appName = "Toto je moja aplikacia";
        int number = 1234;
        String string = "string";
        model.addAttribute("number", number);
        model.addAttribute("string", string);
        model.addAttribute("appName", appName);

        // Generate GeoJSON data with multiple markers
        GeoJsonFeatureCollection featureCollection = new GeoJsonFeatureCollection();
        featureCollection.setType("FeatureCollection");

        List<GeoJsonFeature> features = new ArrayList<>();

        List<Device> devices = deviceRepository.findAll();

        if(devices.size() > 0) {
            for(Device device: devices) {
                if(device.getDashboardLink() != null) {
                    GeoJsonFeature feature = GeoJsonFeature.createGeoJsonFeature(
                            device.getName(), device.getLongitude(), device.getLatitude(),
                            device.getOwner(), device.getDashboardLink());
                    features.add(feature);
                } else {
                    GeoJsonFeature feature = GeoJsonFeature.createGeoJsonFeature(
                            device.getName(), device.getLongitude(), device.getLatitude(),
                            device.getOwner(), "unknown");
                    features.add(feature);
                }
            }
        }

        featureCollection.setFeatures(features);

        model.addAttribute("geoJsonData", featureCollection);

        return "home";
    }

    @GetMapping("/mapa")
    public String getMap() {
        return "deviceList";
    }

}
