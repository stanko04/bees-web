package sk.fiit.bp.DataGenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.fiit.bp.DataGenerator.model.database.Device;
import sk.fiit.bp.DataGenerator.repository.DeviceRepository;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private DeviceRepository deviceRepository;


    @GetMapping("get-devices")
    public List<Device> getAllDevices(){
        return deviceRepository.findAll();
    }


}