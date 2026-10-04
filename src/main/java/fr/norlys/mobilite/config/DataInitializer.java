package fr.norlys.mobilite.config;

import fr.norlys.mobilite.entity.Agent;
import fr.norlys.mobilite.entity.AgentRole;
import fr.norlys.mobilite.entity.Station;
import fr.norlys.mobilite.entity.Vehicle;
import fr.norlys.mobilite.entity.VehicleStatus;
import fr.norlys.mobilite.entity.VehicleType;
import fr.norlys.mobilite.repository.AgentRepository;
import fr.norlys.mobilite.repository.StationRepository;
import fr.norlys.mobilite.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Insère un jeu de données de démarrage lorsque la base est vide,
 * afin que l'application soit utilisable dès le premier lancement.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AgentRepository agentRepository;
    private final StationRepository stationRepository;
    private final VehicleRepository vehicleRepository;

    public DataInitializer(AgentRepository agentRepository,
                           StationRepository stationRepository,
                           VehicleRepository vehicleRepository) {
        this.agentRepository = agentRepository;
        this.stationRepository = stationRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void run(String... args) {
        if (agentRepository.count() > 0) {
            log.info("Base déjà initialisée, aucune donnée de démarrage insérée");
            return;
        }

        Agent tverdier = agent("tverdier", "lead2024", "Thomas Verdier",
                "thomas.verdier@norlys-mobilite.fr", "06 12 34 56 78", AgentRole.SUPERVISEUR, true);
        Agent mlefranc = agent("mlefranc", "stationA", "Marion Lefranc",
                "marion.lefranc@norlys-mobilite.fr", "06 23 45 67 89", AgentRole.SUPERVISEUR, true);
        Agent kdiallo = agent("kdiallo", "velo123", "Karim Diallo",
                "karim.diallo@norlys-mobilite.fr", "06 34 56 78 90", AgentRole.TERRAIN, true);
        Agent sbernard = agent("sbernard", "trott2023", "Sophie Bernard",
                "sophie.bernard@norlys-mobilite.fr", "06 45 67 89 01", AgentRole.TERRAIN, false);
        agentRepository.saveAll(java.util.List.of(tverdier, mlefranc, kdiallo, sbernard));

        Station gareCentrale = station("Gare Centrale", "1 place de la Gare", 20, 48.8412, 2.3218, tverdier);
        Station placeDuMarche = station("Place du Marché", "14 rue des Halles", 12, 48.8455, 2.3301, mlefranc);
        Station campusNord = station("Campus Nord", "Avenue de l'Université", 6, 48.8620, 2.3410, null);
        stationRepository.saveAll(java.util.List.of(gareCentrale, placeDuMarche, campusNord));

        vehicleRepository.saveAll(java.util.List.of(
                vehicle("NRL-B-001", VehicleType.BIKE, 92, VehicleStatus.AVAILABLE, gareCentrale),
                vehicle("NRL-B-002", VehicleType.BIKE, 15, VehicleStatus.LOW_BATTERY, gareCentrale),
                vehicle("NRL-B-003", VehicleType.BIKE, 60, VehicleStatus.MAINTENANCE, gareCentrale),
                vehicle("NRL-S-001", VehicleType.SCOOTER, 80, VehicleStatus.AVAILABLE, placeDuMarche),
                vehicle("NRL-S-002", VehicleType.SCOOTER, 45, VehicleStatus.IN_USE, null),
                vehicle("NRL-S-003", VehicleType.SCOOTER, 100, VehicleStatus.AVAILABLE, campusNord),
                vehicle("NRL-S-004", VehicleType.SCOOTER, 33, VehicleStatus.AVAILABLE, campusNord),
                vehicle("NRL-B-004", VehicleType.BIKE, 0, VehicleStatus.OUT_OF_SERVICE, null)
        ));

        log.info("Données de démarrage insérées : {} agents, {} stations, {} véhicules",
                agentRepository.count(), stationRepository.count(), vehicleRepository.count());
    }

    private Agent agent(String username, String password, String fullName, String email,
                        String phone, AgentRole role, boolean active) {
        Agent agent = new Agent();
        agent.setUsername(username);
        agent.setPassword(password);
        agent.setFullName(fullName);
        agent.setEmail(email);
        agent.setPhone(phone);
        agent.setRole(role);
        agent.setActive(active);
        return agent;
    }

    private Station station(String name, String address, int capacity,
                            double latitude, double longitude, Agent manager) {
        Station station = new Station();
        station.setName(name);
        station.setAddress(address);
        station.setCapacity(capacity);
        station.setLatitude(latitude);
        station.setLongitude(longitude);
        station.setActive(true);
        station.setManager(manager);
        return station;
    }

    private Vehicle vehicle(String serialNumber, VehicleType type, int batteryLevel,
                            VehicleStatus status, Station station) {
        Vehicle vehicle = new Vehicle();
        vehicle.setSerialNumber(serialNumber);
        vehicle.setType(type);
        vehicle.setBatteryLevel(batteryLevel);
        vehicle.setStatus(status);
        vehicle.setStation(station);
        return vehicle;
    }
}
