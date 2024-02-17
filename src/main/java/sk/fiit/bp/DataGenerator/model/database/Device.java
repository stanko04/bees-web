package sk.fiit.bp.DataGenerator.model.database;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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


}
