package sk.fiit.bp.DataGenerator.model.beeHive.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.beeHive.Device;

import java.util.List;

@Data
public class DevicesWrapper {
    private List<Device> data;
}
