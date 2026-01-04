package com.example.application.entity;

public enum CardBrand {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMERICAN_EXPRESS("American Express"),
    MAESTRO("Maestro"),
    DISCOVER("Discover"),
    OTHER("Inna");

    private final String displayName;

    CardBrand(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CardBrand detectFromNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return OTHER;
        }

        String cleaned = cardNumber.replaceAll("\\s+", "");

        if (cleaned.startsWith("4")) {
            return VISA;
        } else if (cleaned.matches("^5[1-5].*") || cleaned.matches("^2[2-7].*")) {
            return MASTERCARD;
        } else if (cleaned.matches("^3[47].*")) {
            return AMERICAN_EXPRESS;
        } else if (cleaned.matches("^(5018|5020|5038|6304|6759|6761|6763).*")) {
            return MAESTRO;
        } else if (cleaned.matches("^6011.*") || cleaned.matches("^65.*")) {
            return DISCOVER;
        }

        return OTHER;
    }
}

