package fr.norlys.mobilite.entity;

/**
 * Cycle de vie d'un véhicule de la flotte.
 * AVAILABLE      : en station, louable.
 * IN_USE         : en cours de location, hors station.
 * LOW_BATTERY    : en station, batterie sous le seuil de location.
 * MAINTENANCE    : immobilisé pour intervention technique.
 * OUT_OF_SERVICE : retiré définitivement de la flotte.
 */
public enum VehicleStatus {
    AVAILABLE,
    IN_USE,
    LOW_BATTERY,
    MAINTENANCE,
    OUT_OF_SERVICE
}
