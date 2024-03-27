package sk.fiit.bp.BeesWeb.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.BeesWeb.model.mapDevice.DeviceTemp;

import java.util.List;

@Data
public class DevicesWrapper {
    private List<DeviceTemp> data;
}
