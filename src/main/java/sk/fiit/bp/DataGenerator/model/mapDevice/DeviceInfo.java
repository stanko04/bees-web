package sk.fiit.bp.DataGenerator.model.mapDevice;

import lombok.Data;

@Data
public class DeviceInfo {
    private String name;
    private String dashboard;
    private double latitude;
    private double longitude;
    private String owner;

    public DeviceInfo(String dashboard, double latitude, double longitude) {
        this.dashboard = dashboard;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public DeviceInfo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
