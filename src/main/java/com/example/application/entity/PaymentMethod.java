package com.example.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PaymentMethod extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;
    private String cardHolderName;
    @Column(length = 4)
    private String cardLastFourDigits;
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;
    private String expiryMonth;
    private String expiryYear;
    private boolean isDefault = false;
    @Column(nullable = false)
    private LocalDateTime addedDate;
    @Column(unique = true)
    private String paymentToken;

    public String getMaskedCardNumber() {
        if (cardLastFourDigits != null) {
            return "**** **** **** " + cardLastFourDigits;
        }
        return "";
    }

    public String getDisplayName() {
        if (paymentType == PaymentType.CARD && cardBrand != null) {
            return cardBrand.getDisplayName() + " •••• " + cardLastFourDigits;
        }
        return paymentType.getDisplayName();
    }

    public boolean isExpired() {
        int currentYear = java.time.Year.now().getValue() % 100;
        int currentMonth = java.time.LocalDate.now().getMonthValue();

        int cardYear = Integer.parseInt(expiryYear);
        int cardMonth = Integer.parseInt(expiryMonth);

        if (cardYear < currentYear) {
            return true;
        }
        if (cardYear == currentYear && cardMonth < currentMonth) {
            return true;
        }
        return false;
    }
}
