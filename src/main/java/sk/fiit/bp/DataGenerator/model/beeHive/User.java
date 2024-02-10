package sk.fiit.bp.DataGenerator.model.beeHive;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.UserAdditionalInfo;

@Data
public class User {
    private Id id;
    private String firstName;
    private String lastName;
    private UserAdditionalInfo additionalInfo;
}
