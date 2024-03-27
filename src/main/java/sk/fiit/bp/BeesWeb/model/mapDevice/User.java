package sk.fiit.bp.BeesWeb.model.mapDevice;

import lombok.Data;

@Data
public class User {
    private Id id;
    private String firstName;
    private String lastName;
    private UserAdditionalInfo additionalInfo;
}
