package com.ancrelieu.reservation.service;

import com.ancrelieu.reservation.entity.Berth;
import com.ancrelieu.reservation.entity.Reservation;
import com.ancrelieu.reservation.entity.ReservationStatus;
import com.ancrelieu.reservation.entity.User;
import com.ancrelieu.reservation.exception.ConflictException;
import com.ancrelieu.reservation.exception.NotFoundException;
import com.ancrelieu.reservation.repository.BerthRepository;
import com.ancrelieu.reservation.repository.ReservationRepository;
import com.ancrelieu.reservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Règles de création, de disponibilité et d'annulation des réservations.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BerthRepository berthRepository;
    private final UserRepository userRepository;
    private final BillingService billingService;

    public ReservationService(ReservationRepository reservationRepository,
                              BerthRepository berthRepository,
                              UserRepository userRepository,
                              BillingService billingService) {
        this.reservationRepository = reservationRepository;
        this.berthRepository = berthRepository;
        this.userRepository = userRepository;
        this.billingService = billingService;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable"));
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public boolean isBerthAvailable(Long berthId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> confirmed =
                reservationRepository.findByBerthIdAndStatus(berthId, ReservationStatus.CONFIRMED);
        return confirmed.stream().noneMatch(existing ->
                startDate.isBefore(existing.getEndDate()) && endDate.isAfter(existing.getStartDate()));
    }

    @Transactional
    public Reservation createReservation(Long userId, Long berthId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Berth berth = berthRepository.findById(berthId)
                .orElseThrow(() -> new NotFoundException("Emplacement introuvable"));

        if (!startDate.isBefore(endDate)) {
            throw new ConflictException("La date de début doit précéder la date de fin");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new ConflictException("La date de début ne peut pas être passée");
        }
        if (!isBerthAvailable(berthId, startDate, endDate)) {
            throw new ConflictException("Emplacement indisponible sur cette période");
        }

        BigDecimal totalAmount = billingService.calculateTotalAmount(berth, startDate, endDate);
        return reservationRepository.save(new Reservation(user, berth, startDate, endDate, totalAmount));
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ConflictException("Cette réservation est déjà annulée");
        }

        BigDecimal refund = billingService.calculateRefund(reservation, LocalDate.now());
        reservation.setRefundAmount(refund);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        return reservationRepository.save(reservation);
    }
}
