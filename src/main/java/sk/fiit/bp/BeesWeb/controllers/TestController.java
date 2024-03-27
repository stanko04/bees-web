package sk.fiit.bp.BeesWeb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.fiit.bp.BeesWeb.model.database.Device;
import sk.fiit.bp.BeesWeb.repository.DeviceRepository;

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