package com.ancrelieu.reservation.dto;

import com.ancrelieu.reservation.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        Long userId,
        Long berthId,
        String berthName,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        String status,
        BigDecimal refundAmount
) {

    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getUser().getId(),
                r.getBerth().getId(),
                r.getBerth().getName(),
                r.getStartDate(),
                r.getEndDate(),
                r.getTotalAmount(),
                r.getStatus().name(),
                r.getRefundAmount()
        );
    }
}
