package fr.norlys.mobilite.repository;

import fr.norlys.mobilite.entity.Vehicle;
import fr.norlys.mobilite.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStationId(Long stationId);

    long countByStationId(Long stationId);

    List<Vehicle> findByStatus(VehicleStatus status);

    Optional<Vehicle> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
