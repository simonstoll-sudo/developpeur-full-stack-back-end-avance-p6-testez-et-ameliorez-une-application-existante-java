package com.ancrelieu.reservation.controller;

import com.ancrelieu.reservation.dto.ReservationRequest;
import com.ancrelieu.reservation.dto.ReservationResponse;
import com.ancrelieu.reservation.entity.Reservation;
import com.ancrelieu.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservationById(@PathVariable Long id) {
        return ReservationResponse.from(reservationService.getReservationById(id));
    }

    @GetMapping("/user/{userId}")
    public List<ReservationResponse> getReservationsByUser(@PathVariable Long userId) {
        return reservationService.getReservationsByUser(userId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.createReservation(
                request.userId(), request.berthId(), request.startDate(), request.endDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationResponse.from(reservation));
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancelReservation(@PathVariable Long id) {
        return ReservationResponse.from(reservationService.cancelReservation(id));
    }
}
