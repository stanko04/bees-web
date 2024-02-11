package sk.fiit.bp.DataGenerator.model.mapDevice;

import lombok.Data;

@Data
public class User {
    private Id id;
    private String firstName;
    private String lastName;
    private UserAdditionalInfo additionalInfo;
}
