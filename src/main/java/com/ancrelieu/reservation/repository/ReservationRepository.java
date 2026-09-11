package com.ancrelieu.reservation.repository;

import com.ancrelieu.reservation.entity.Reservation;
import com.ancrelieu.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByBerthIdAndStatus(Long berthId, ReservationStatus status);
}
