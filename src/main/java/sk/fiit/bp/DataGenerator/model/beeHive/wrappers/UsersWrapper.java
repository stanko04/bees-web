package sk.fiit.bp.DataGenerator.model.beeHive.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.beeHive.User;

import java.util.List;

@Data
public class UsersWrapper {
    private List<User> data;
}
