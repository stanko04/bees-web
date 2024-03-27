package sk.fiit.bp.BeesWeb.model.mapDevice.wrappers;

import lombok.Data;
import sk.fiit.bp.BeesWeb.model.mapDevice.Tenant;

import java.util.List;

@Data
public class TenantsWrapper {
    private List<Tenant> data;
}
