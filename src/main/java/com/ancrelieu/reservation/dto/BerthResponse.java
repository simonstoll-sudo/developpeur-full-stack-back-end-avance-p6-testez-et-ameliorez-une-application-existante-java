package com.ancrelieu.reservation.dto;

import com.ancrelieu.reservation.entity.Berth;

import java.math.BigDecimal;

public record BerthResponse(
        Long id,
        String name,
        String harbor,
        BigDecimal dailyRate,
        int maxBoatLength,
        boolean active
) {

    public static BerthResponse from(Berth b) {
        return new BerthResponse(b.getId(), b.getName(), b.getHarbor(), b.getDailyRate(),
                b.getMaxBoatLength(), b.isActive());
    }
}
