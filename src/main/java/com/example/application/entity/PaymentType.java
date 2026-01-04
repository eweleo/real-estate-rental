package com.example.application.entity;

public enum PaymentType {
    CARD("Karta płatnicza"),
    BLIK("BLIK"),
    BANK_TRANSFER("Przelew bankowy");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
