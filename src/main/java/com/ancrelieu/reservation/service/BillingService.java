package com.ancrelieu.reservation.service;

import com.ancrelieu.reservation.entity.Berth;
import com.ancrelieu.reservation.entity.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Règles de tarification des séjours et de remboursement à l'annulation.
 */
@Service
public class BillingService {

    private static final BigDecimal LONG_STAY_DISCOUNT = new BigDecimal("0.90");
    private static final int LONG_STAY_THRESHOLD_NIGHTS = 7;

    private static final int FULL_REFUND_DAYS = 7;
    private static final int PARTIAL_REFUND_DAYS = 2;
    private static final BigDecimal PARTIAL_REFUND_RATE = new BigDecimal("0.50");

    /**
     * Calcule le montant total d'un séjour : nombre de nuits multiplié par le tarif
     * journalier de l'emplacement, avec une remise de 10 % à partir de 7 nuits.
     */
    public BigDecimal calculateTotalAmount(Berth berth, LocalDate startDate, LocalDate endDate) {
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal total = berth.getDailyRate().multiply(BigDecimal.valueOf(nights));

        if (nights >= LONG_STAY_THRESHOLD_NIGHTS) {
            total = total.multiply(LONG_STAY_DISCOUNT);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule le montant remboursé lors d'une annulation, selon le barème contractuel :
     * remboursement intégral à 7 jours ou plus du début du séjour, remboursement de
     * 50 % entre 2 et 6 jours, aucun remboursement en dessous.
     */
    public BigDecimal calculateRefund(Reservation reservation, LocalDate cancellationDate) {
        long daysBefore = ChronoUnit.DAYS.between(reservation.getStartDate(), cancellationDate);

        BigDecimal refund;
        if (daysBefore >= FULL_REFUND_DAYS) {
            refund = reservation.getTotalAmount();
        } else if (daysBefore >= PARTIAL_REFUND_DAYS) {
            refund = reservation.getTotalAmount().multiply(PARTIAL_REFUND_RATE);
        } else {
            refund = BigDecimal.ZERO;
        }

        return refund.setScale(2, RoundingMode.HALF_UP);
    }
}
