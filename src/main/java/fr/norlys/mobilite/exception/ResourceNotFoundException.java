package fr.norlys.mobilite.exception;

/**
 * Levée lorsqu'une ressource demandée (station, véhicule, agent) n'existe pas.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
