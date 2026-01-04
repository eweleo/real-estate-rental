package com.example.application.views.transaction;

import com.example.application.entity.ReservationStatus;

public class TransactionFilterHelper {

    public static ReservationStatus mapStatusFromDisplay(String displayStatus) {
        return switch (displayStatus) {
            case "Oczekująca" -> ReservationStatus.PENDING;
            case "Potwierdzona" -> ReservationStatus.CONFIRMED;
            case "Anulowana" -> ReservationStatus.CANCELLED;
            case "Zakończona" -> ReservationStatus.COMPLETED;
            default -> null;
        };
    }
}