package sk.fiit.bp.BeesWeb.model.database;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private String dashboardLink;

    private String owner;

    private String city;

    private String accessToken;

    @Transient
    private String description;

    public Device(String dashboardLink, double latitude, double longitude, String city) {
        this.dashboardLink = dashboardLink;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }

    public Device(String dashboardLink, double latitude, double longitude) {
        this.dashboardLink = dashboardLink;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Device(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Device() {

    }

    public Device(String dashboardLink) {
        this.dashboardLink = dashboardLink;
    }

    public Device (Long id, String city, String owner, String name, String accessToken) {
        this.id = id;
        this.city = city;
        this.owner = owner;
        this.name = name;
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id) && Objects.equals(name, device.name) &&
                Objects.equals(latitude, device.latitude) && Objects.equals(longitude, device.longitude) &&
                Objects.equals(dashboardLink, device.dashboardLink) && Objects.equals(owner, device.owner) &&
                Objects.equals(city, device.city) && Objects.equals(accessToken, device.accessToken) &&
                Objects.equals(description, device.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, latitude, longitude, dashboardLink, owner, city, accessToken, description);
    }
}
