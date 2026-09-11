package com.ancrelieu.reservation.config;

import com.ancrelieu.reservation.entity.Berth;
import com.ancrelieu.reservation.entity.Reservation;
import com.ancrelieu.reservation.entity.ReservationStatus;
import com.ancrelieu.reservation.entity.User;
import com.ancrelieu.reservation.repository.BerthRepository;
import com.ancrelieu.reservation.repository.ReservationRepository;
import com.ancrelieu.reservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Insère un jeu de données de démarrage lorsque la base est vide,
 * afin que l'application soit utilisable dès le premier lancement.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BerthRepository berthRepository;
    private final ReservationRepository reservationRepository;

    public DataInitializer(UserRepository userRepository,
                           BerthRepository berthRepository,
                           ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.berthRepository = berthRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User marina = userRepository.save(new User("marina.dupre@exemple.fr", "voilier2024", "Marina Dupré"));
        User theo = userRepository.save(new User("theo.lambert@exemple.fr", "catamaran77", "Théo Lambert"));
        User ines = userRepository.save(new User("ines.moreau@exemple.fr", "grandlarge!", "Inès Moreau"));

        Berth quaiNordA1 = berthRepository.save(
                new Berth("Quai Nord A1", "Port de Kervelen", new BigDecimal("35.00"), 8, true));
        Berth quaiNordA2 = berthRepository.save(
                new Berth("Quai Nord A2", "Port de Kervelen", new BigDecimal("48.00"), 10, true));
        Berth pontonB4 = berthRepository.save(
                new Berth("Ponton B4", "Port de Kervelen", new BigDecimal("62.00"), 12, true));
        Berth pontonC2 = berthRepository.save(
                new Berth("Ponton C2", "Marina de Saint-Elban", new BigDecimal("85.00"), 15, true));
        Berth digueGrandLarge = berthRepository.save(
                new Berth("Digue Grand Large", "Marina de Saint-Elban", new BigDecimal("120.00"), 22, true));

        LocalDate today = LocalDate.now();

        // 3 nuits x 35.00 = 105.00
        reservationRepository.save(new Reservation(
                marina, quaiNordA1,
                today.plusDays(10), today.plusDays(13),
                new BigDecimal("105.00")));

        // 8 nuits x 62.00 = 496.00, remise 10 % au-delà de 7 nuits = 446.40
        reservationRepository.save(new Reservation(
                theo, pontonB4,
                today.plusDays(20), today.plusDays(28),
                new BigDecimal("446.40")));

        // Séjour passé : 5 nuits x 48.00 = 240.00
        Reservation pastStay = new Reservation(
                ines, quaiNordA2,
                today.minusDays(30), today.minusDays(25),
                new BigDecimal("240.00"));
        reservationRepository.save(pastStay);

        // 4 nuits x 120.00 = 480.00
        Reservation cancelled = new Reservation(
                marina, digueGrandLarge,
                today.plusDays(40), today.plusDays(44),
                new BigDecimal("480.00"));
        cancelled.setStatus(ReservationStatus.CANCELLED);
        cancelled.setRefundAmount(new BigDecimal("0.00"));
        cancelled.setCancelledAt(LocalDateTime.now().minusDays(2));
        reservationRepository.save(cancelled);
    }
}
