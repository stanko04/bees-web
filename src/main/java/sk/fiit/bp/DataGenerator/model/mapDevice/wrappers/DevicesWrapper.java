package sk.fiit.bp.DataGenerator.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.mapDevice.Device;

import java.util.List;

@Data
public class DevicesWrapper {
    private List<Device> data;
}
