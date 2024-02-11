package sk.fiit.bp.DataGenerator.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.mapDevice.Tenant;

import java.util.List;

@Data
public class TenantsWrapper {
    private List<Tenant> data;
}
