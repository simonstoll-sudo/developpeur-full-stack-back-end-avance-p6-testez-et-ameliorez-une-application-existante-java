package com.ancrelieu.reservation.controller;

import com.ancrelieu.reservation.dto.BerthResponse;
import com.ancrelieu.reservation.repository.BerthRepository;
import com.ancrelieu.reservation.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/berths")
public class BerthController {

    private final BerthRepository berthRepository;
    private final ReservationService reservationService;

    public BerthController(BerthRepository berthRepository, ReservationService reservationService) {
        this.berthRepository = berthRepository;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<BerthResponse> getActiveBerths() {
        return berthRepository.findByActiveTrue().stream()
                .map(BerthResponse::from)
                .toList();
    }

    @GetMapping("/{id}/availability")
    public Map<String, Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Map.of("available", reservationService.isBerthAvailable(id, start, end));
    }
}
