package sk.fiit.bp.DataGenerator.model.beeHive.wrappers;

import lombok.Data;
import sk.fiit.bp.DataGenerator.model.beeHive.Tenant;

import java.util.List;

@Data
public class TenantsWrapper {
    private List<Tenant> data;
}
