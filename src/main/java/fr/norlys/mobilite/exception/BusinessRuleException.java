package fr.norlys.mobilite.exception;

/**
 * Levée lorsqu'une opération viole une règle de gestion
 * (station pleine, véhicule non disponible, capacité invalide...).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
