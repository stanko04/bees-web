package sk.fiit.bp.BeesWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.fiit.bp.BeesWeb.model.database.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
