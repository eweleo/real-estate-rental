package com.example.application.entity;


public enum ReservationStatus {
    PENDING("Oczekująca"),
    CONFIRMED("Potwierdzona"),
    CANCELLED("Anulowana"),
    COMPLETED("Zakończona");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}