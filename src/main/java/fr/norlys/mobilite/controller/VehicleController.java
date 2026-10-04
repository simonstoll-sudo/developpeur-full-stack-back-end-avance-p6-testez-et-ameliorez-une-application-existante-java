package fr.norlys.mobilite.controller;

import fr.norlys.mobilite.entity.Vehicle;
import fr.norlys.mobilite.service.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Véhicules", description = "Vélos et trottinettes de la flotte")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> findAll() {
        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public Vehicle findById(@PathVariable Long id) {
        return vehicleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.create(vehicle);
    }

    @PutMapping("/{id}/station/{stationId}")
    public Vehicle assignToStation(@PathVariable Long id, @PathVariable Long stationId) {
        return vehicleService.assignToStation(id, stationId);
    }

    @PostMapping("/{id}/rental/start")
    public Vehicle startRental(@PathVariable Long id) {
        return vehicleService.startRental(id);
    }

    @PostMapping("/{id}/rental/end")
    public Vehicle endRental(@PathVariable Long id,
                             @RequestParam Long stationId,
                             @RequestParam int batteryLevel) {
        return vehicleService.endRental(id, stationId, batteryLevel);
    }

    @PutMapping("/{id}/battery")
    public Vehicle updateBattery(@PathVariable Long id, @RequestParam int level) {
        return vehicleService.updateBattery(id, level);
    }

    @PostMapping("/{id}/maintenance")
    public Vehicle sendToMaintenance(@PathVariable Long id) {
        return vehicleService.sendToMaintenance(id);
    }

    @PostMapping("/{id}/maintenance/return")
    public Vehicle returnFromMaintenance(@PathVariable Long id) {
        return vehicleService.returnFromMaintenance(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable Long id) {
        vehicleService.retire(id);
    }
}
