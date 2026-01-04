package com.example.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Reservation extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String reservationNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer guestsNumber;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime confirmedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column(length = 1000)
    private String notes;

    @Column(length = 1000)
    private String cancellationReason;

    public long getNightsCount() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    @PrePersist
    public void generateIdentifiers() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = java.util.UUID.randomUUID().toString();
        }
        if (this.reservationNumber == null || this.reservationNumber.isEmpty()) {
            this.reservationNumber = generateReservationNumber();
        }
    }

    private String generateReservationNumber() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%05d", new java.util.Random().nextInt(100000));
        return "RES-" + date + "-" + random;
    }
}
