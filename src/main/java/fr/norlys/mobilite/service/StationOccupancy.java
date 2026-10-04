package fr.norlys.mobilite.service;

/**
 * Vue calculée de l'occupation d'une station à un instant donné.
 *
 * @param stationId      identifiant de la station
 * @param capacity       nombre de places de la station
 * @param vehicleCount   nombre de véhicules actuellement rattachés
 * @param availableSlots places restantes (jamais négatif)
 * @param occupancyRate  ratio véhicules / capacité, arrondi à deux décimales
 */
public record StationOccupancy(
        Long stationId,
        int capacity,
        long vehicleCount,
        int availableSlots,
        double occupancyRate
) {
}
