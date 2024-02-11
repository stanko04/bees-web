package sk.fiit.bp.DataGenerator.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.mapDevice.User;

import java.util.List;

@Data
public class UsersWrapper {
    private List<User> data;
}
