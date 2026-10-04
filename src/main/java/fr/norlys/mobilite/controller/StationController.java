package fr.norlys.mobilite.controller;

import fr.norlys.mobilite.entity.Station;
import fr.norlys.mobilite.entity.Vehicle;
import fr.norlys.mobilite.service.StationOccupancy;
import fr.norlys.mobilite.service.StationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@Tag(name = "Stations", description = "Stations de la flotte et leur occupation")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> findAll() {
        return stationService.findAll();
    }

    @GetMapping("/{id}")
    public Station findById(@PathVariable Long id) {
        return stationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Station create(@RequestBody Station station) {
        return stationService.create(station);
    }

    @PutMapping("/{id}")
    public Station update(@PathVariable Long id, @RequestBody Station station) {
        return stationService.update(id, station);
    }

    @PutMapping("/{id}/manager/{agentId}")
    public Station assignManager(@PathVariable Long id, @PathVariable Long agentId) {
        return stationService.assignManager(id, agentId);
    }

    @GetMapping("/{id}/vehicles")
    public List<Vehicle> vehicles(@PathVariable Long id) {
        return stationService.vehiclesOf(id);
    }

    @GetMapping("/{id}/occupancy")
    public StationOccupancy occupancy(@PathVariable Long id) {
        return stationService.occupancy(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        stationService.deactivate(id);
    }
}
