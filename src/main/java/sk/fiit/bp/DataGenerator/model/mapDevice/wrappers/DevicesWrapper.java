package sk.fiit.bp.DataGenerator.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.mapDevice.DeviceTemp;

import java.util.List;

@Data
public class DevicesWrapper {
    private List<DeviceTemp> data;
}
