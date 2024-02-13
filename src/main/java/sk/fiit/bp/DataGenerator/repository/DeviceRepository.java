package sk.fiit.bp.DataGenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.fiit.bp.DataGenerator.model.database.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
