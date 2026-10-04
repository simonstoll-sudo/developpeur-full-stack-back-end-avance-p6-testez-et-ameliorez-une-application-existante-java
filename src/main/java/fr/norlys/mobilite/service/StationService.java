package fr.norlys.mobilite.service;

import fr.norlys.mobilite.entity.Agent;
import fr.norlys.mobilite.entity.AgentRole;
import fr.norlys.mobilite.entity.Station;
import fr.norlys.mobilite.entity.Vehicle;
import fr.norlys.mobilite.exception.BusinessRuleException;
import fr.norlys.mobilite.exception.ResourceNotFoundException;
import fr.norlys.mobilite.repository.AgentRepository;
import fr.norlys.mobilite.repository.StationRepository;
import fr.norlys.mobilite.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Règles de gestion des stations : capacité, responsable, désactivation, occupation.
 */
@Service
@Transactional
public class StationService {

    private final StationRepository stationRepository;
    private final VehicleRepository vehicleRepository;
    private final AgentRepository agentRepository;

    public StationService(StationRepository stationRepository,
                          VehicleRepository vehicleRepository,
                          AgentRepository agentRepository) {
        this.stationRepository = stationRepository;
        this.vehicleRepository = vehicleRepository;
        this.agentRepository = agentRepository;
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station introuvable : " + id));
    }

    public Station create(Station station) {
        requirePositiveCapacity(station.getCapacity());
        station.setId(null);
        station.setActive(true);
        return stationRepository.save(station);
    }

    public Station update(Long id, Station changes) {
        Station station = findById(id);
        requirePositiveCapacity(changes.getCapacity());

        long vehiclesPresent = vehicleRepository.countByStationId(id);
        if (changes.getCapacity() < vehiclesPresent) {
            throw new BusinessRuleException("La capacité ne peut pas être inférieure au nombre de véhicules présents");
        }

        station.setName(changes.getName());
        station.setAddress(changes.getAddress());
        station.setCapacity(changes.getCapacity());
        station.setLatitude(changes.getLatitude());
        station.setLongitude(changes.getLongitude());
        return stationRepository.save(station);
    }

    public Station assignManager(Long stationId, Long agentId) {
        Station station = findById(stationId);
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable : " + agentId));

        if (!agent.isActive()) {
            throw new BusinessRuleException("Un agent désactivé ne peut pas gérer une station");
        }
        if (agent.getRole() != AgentRole.SUPERVISEUR) {
            throw new BusinessRuleException("Seul un superviseur peut gérer une station");
        }

        station.setManager(agent);
        return stationRepository.save(station);
    }

    public void deactivate(Long id) {
        Station station = findById(id);
        if (vehicleRepository.countByStationId(id) > 0) {
            throw new BusinessRuleException("Une station occupée ne peut pas être désactivée");
        }
        station.setActive(false);
        stationRepository.save(station);
    }

    public List<Vehicle> vehiclesOf(Long stationId) {
        findById(stationId);
        return vehicleRepository.findByStationId(stationId);
    }

    public StationOccupancy occupancy(Long stationId) {
        Station station = findById(stationId);
        long vehicleCount = vehicleRepository.countByStationId(stationId);
        int availableSlots = (int) Math.max(0, station.getCapacity() - vehicleCount);
        double occupancyRate = Math.round(vehicleCount * 100.0 / station.getCapacity()) / 100.0;
        return new StationOccupancy(station.getId(), station.getCapacity(), vehicleCount, availableSlots, occupancyRate);
    }

    public boolean hasFreeSlot(Station station) {
        return vehicleRepository.countByStationId(station.getId()) < station.getCapacity();
    }

    private void requirePositiveCapacity(int capacity) {
        if (capacity <= 0) {
            throw new BusinessRuleException("La capacité d'une station doit être strictement positive");
        }
    }
}
