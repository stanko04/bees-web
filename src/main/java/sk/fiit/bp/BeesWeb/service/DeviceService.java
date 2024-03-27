package sk.fiit.bp.BeesWeb.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import sk.fiit.bp.BeesWeb.model.database.Device;

import java.util.HashMap;
import java.util.List;

@Service
@Data
public class DeviceService {
    private static HashMap<Long, Device> devices = new HashMap<>();

    public static synchronized void updateHashMap(List<Device> deviceList) {
        devices.clear();

        for (Device device : deviceList) {
            devices.put(device.getId(), device);
        }
    }

    public static synchronized HashMap<Long, Device> getDevices() {
        return devices;
    }
}
