package sk.fiit.bp.BeesWeb.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.BeesWeb.model.mapDevice.User;

import java.util.List;

@Data
public class UsersWrapper {
    private List<User> data;
}
