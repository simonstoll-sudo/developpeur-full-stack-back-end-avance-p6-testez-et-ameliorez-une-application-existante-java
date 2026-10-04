package fr.norlys.mobilite.service;

import fr.norlys.mobilite.entity.Station;
import fr.norlys.mobilite.entity.Vehicle;
import fr.norlys.mobilite.entity.VehicleStatus;
import fr.norlys.mobilite.exception.BusinessRuleException;
import fr.norlys.mobilite.exception.ResourceNotFoundException;
import fr.norlys.mobilite.repository.StationRepository;
import fr.norlys.mobilite.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Règles de gestion de la flotte : affectation en station, location, batterie, maintenance, retrait.
 */
@Service
@Transactional
public class VehicleService {

    /** Niveau de batterie minimal pour qu'un véhicule soit louable. */
    public static final int MIN_BATTERY_FOR_RENTAL = 20;

    /** Niveau de batterie maximal. */
    public static final int MAX_BATTERY_LEVEL = 100;

    private final VehicleRepository vehicleRepository;
    private final StationRepository stationRepository;

    public VehicleService(VehicleRepository vehicleRepository, StationRepository stationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.stationRepository = stationRepository;
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable : " + id));
    }

    public Vehicle create(Vehicle vehicle) {
        if (vehicleRepository.existsBySerialNumber(vehicle.getSerialNumber())) {
            throw new BusinessRuleException("Numéro de série déjà utilisé : " + vehicle.getSerialNumber());
        }
        requireValidBattery(vehicle.getBatteryLevel());

        vehicle.setId(null);
        vehicle.setStation(null);
        vehicle.setStatus(statusForBattery(vehicle.getBatteryLevel()));
        return vehicleRepository.save(vehicle);
    }

    public Vehicle assignToStation(Long vehicleId, Long stationId) {
        Vehicle vehicle = findById(vehicleId);
        Station station = findStation(stationId);

        requireActiveStation(station);
        if (vehicle.getStatus() == VehicleStatus.IN_USE) {
            throw new BusinessRuleException("Un véhicule en cours d'utilisation ne peut pas être affecté");
        }
        if (vehicle.getStatus() == VehicleStatus.OUT_OF_SERVICE) {
            throw new BusinessRuleException("Un véhicule retiré ne peut pas être affecté");
        }
        requireFreeSlot(station);

        vehicle.setStation(station);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle startRental(Long vehicleId) {
        Vehicle vehicle = findById(vehicleId);

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessRuleException("Le véhicule n'est pas disponible");
        }
        if (vehicle.getStation() == null) {
            throw new BusinessRuleException("Le véhicule n'est rattaché à aucune station");
        }
        if (vehicle.getBatteryLevel() < MIN_BATTERY_FOR_RENTAL) {
            throw new BusinessRuleException("Batterie insuffisante pour une location");
        }

        vehicle.setStatus(VehicleStatus.IN_USE);
        vehicle.setStation(null);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle endRental(Long vehicleId, Long stationId, int batteryLevel) {
        Vehicle vehicle = findById(vehicleId);

        if (vehicle.getStatus() != VehicleStatus.IN_USE) {
            throw new BusinessRuleException("Le véhicule n'est pas en cours d'utilisation");
        }
        requireValidBattery(batteryLevel);

        Station station = findStation(stationId);
        requireActiveStation(station);
        requireFreeSlot(station);

        vehicle.setStation(station);
        vehicle.setBatteryLevel(batteryLevel);
        vehicle.setStatus(statusForBattery(batteryLevel));
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateBattery(Long vehicleId, int level) {
        Vehicle vehicle = findById(vehicleId);
        requireValidBattery(level);

        vehicle.setBatteryLevel(level);
        if (vehicle.getStatus() == VehicleStatus.LOW_BATTERY && level >= MIN_BATTERY_FOR_RENTAL) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        } else if (vehicle.getStatus() == VehicleStatus.AVAILABLE && level < MIN_BATTERY_FOR_RENTAL) {
            vehicle.setStatus(VehicleStatus.LOW_BATTERY);
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle sendToMaintenance(Long vehicleId) {
        Vehicle vehicle = findById(vehicleId);

        if (vehicle.getStatus() == VehicleStatus.IN_USE) {
            throw new BusinessRuleException("Un véhicule en cours d'utilisation ne peut pas partir en maintenance");
        }
        if (vehicle.getStatus() == VehicleStatus.OUT_OF_SERVICE) {
            throw new BusinessRuleException("Un véhicule retiré ne peut pas partir en maintenance");
        }

        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle returnFromMaintenance(Long vehicleId) {
        Vehicle vehicle = findById(vehicleId);

        if (vehicle.getStatus() != VehicleStatus.MAINTENANCE) {
            throw new BusinessRuleException("Le véhicule n'est pas en maintenance");
        }

        vehicle.setLastMaintenanceAt(LocalDateTime.now());
        vehicle.setStatus(statusForBattery(vehicle.getBatteryLevel()));
        return vehicleRepository.save(vehicle);
    }

    public void retire(Long vehicleId) {
        Vehicle vehicle = findById(vehicleId);

        if (vehicle.getStatus() == VehicleStatus.IN_USE) {
            throw new BusinessRuleException("Un véhicule en cours d'utilisation ne peut pas être retiré");
        }

        vehicle.setStatus(VehicleStatus.OUT_OF_SERVICE);
        vehicle.setStation(null);
        vehicleRepository.save(vehicle);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Station introuvable : " + stationId));
    }

    private void requireActiveStation(Station station) {
        if (!station.isActive()) {
            throw new BusinessRuleException("La station est désactivée");
        }
    }

    private void requireFreeSlot(Station station) {
        if (vehicleRepository.countByStationId(station.getId()) >= station.getCapacity()) {
            throw new BusinessRuleException("La station est pleine");
        }
    }

    private void requireValidBattery(int level) {
        if (level < 0 || level > MAX_BATTERY_LEVEL) {
            throw new BusinessRuleException("Le niveau de batterie doit être compris entre 0 et 100");
        }
    }

    private VehicleStatus statusForBattery(int level) {
        return level < MIN_BATTERY_FOR_RENTAL ? VehicleStatus.LOW_BATTERY : VehicleStatus.AVAILABLE;
    }
}
