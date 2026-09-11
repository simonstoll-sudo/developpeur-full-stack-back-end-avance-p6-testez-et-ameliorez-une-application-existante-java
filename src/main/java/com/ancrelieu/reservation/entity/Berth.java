package com.ancrelieu.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "berths")
public class Berth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String harbor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    private int maxBoatLength;

    private boolean active;

    public Berth() {
    }

    public Berth(String name, String harbor, BigDecimal dailyRate, int maxBoatLength, boolean active) {
        this.name = name;
        this.harbor = harbor;
        this.dailyRate = dailyRate;
        this.maxBoatLength = maxBoatLength;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHarbor() {
        return harbor;
    }

    public void setHarbor(String harbor) {
        this.harbor = harbor;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public int getMaxBoatLength() {
        return maxBoatLength;
    }

    public void setMaxBoatLength(int maxBoatLength) {
        this.maxBoatLength = maxBoatLength;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
