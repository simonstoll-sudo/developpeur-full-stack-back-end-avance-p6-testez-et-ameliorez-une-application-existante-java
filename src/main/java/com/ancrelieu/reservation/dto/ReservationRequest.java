package com.ancrelieu.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull Long userId,
        @NotNull Long berthId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
