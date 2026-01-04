package com.example.application.views.transaction;

import com.example.application.entity.Reservation;
import com.example.application.entity.ReservationStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TransactionCard {

    public static Div create(Reservation reservation, boolean showingMyReservations) {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "20px")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s");

        card.getElement().addEventListener("mouseenter", e ->
                card.getStyle().set("box-shadow", "0 4px 16px rgba(0,0,0,0.15)"));

        card.getElement().addEventListener("mouseleave", e ->
                card.getStyle().set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)"));

        card.addClickListener(e -> navigateToDetails(reservation, showingMyReservations));

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        mainLayout.getStyle().set("gap", "20px");

        mainLayout.add(
                createImageSection(reservation),
                createInfoSection(reservation, showingMyReservations),
                createPriceSection(reservation, showingMyReservations)
        );

        card.add(mainLayout);
        return card;
    }

    private static void navigateToDetails(Reservation reservation, boolean showingMyReservations) {
        if (showingMyReservations) {
            UI.getCurrent().navigate("reservation-confirmation/" + reservation.getUuid());
        } else {
            UI.getCurrent().navigate("landlord-reservation-details/" + reservation.getUuid());
        }
    }

    private static Div createImageSection(Reservation reservation) {
        Div imageContainer = new Div();

        if (reservation.getApartment().getMainImage() != null) {
            Image thumbnail = new Image();
            StreamResource resource = new StreamResource("thumb",
                    () -> new ByteArrayInputStream(reservation.getApartment().getMainImage().getImageData()));
            thumbnail.setSrc(resource);
            thumbnail.setWidth("120px");
            thumbnail.setHeight("120px");
            thumbnail.getStyle()
                    .set("border-radius", "8px")
                    .set("object-fit", "cover");
            imageContainer.add(thumbnail);
        } else {
            imageContainer.getStyle()
                    .set("width", "120px")
                    .set("height", "120px")
                    .set("background-color", "#e0e0e0")
                    .set("border-radius", "8px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center");
            Icon placeholderIcon = VaadinIcon.PICTURE.create();
            placeholderIcon.setSize("48px");
            placeholderIcon.getStyle().set("color", "#999");
            imageContainer.add(placeholderIcon);
        }

        return imageContainer;
    }

    private static VerticalLayout createInfoSection(Reservation reservation, boolean showingMyReservations) {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);
        infoLayout.getStyle().set("flex", "1");

        H4 apartmentTitle = new H4(reservation.getApartment().getTitle());
        apartmentTitle.getStyle()
                .set("margin", "0 0 8px 0")
                .set("color", "#333");

        infoLayout.add(
                apartmentTitle,
                createLocationRow(reservation),
                createDatesRow(reservation),
                createClientRow(reservation, showingMyReservations),
                createReservationNumberRow(reservation)
        );

        return infoLayout;
    }

    private static HorizontalLayout createLocationRow(Reservation reservation) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.getStyle().set("gap", "5px").set("margin-bottom", "8px");

        Icon icon = VaadinIcon.MAP_MARKER.create();
        icon.setSize("14px");
        icon.getStyle().set("color", "#667eea");

        Span text = new Span(reservation.getApartment().getAddress().getCity());
        text.getStyle().set("color", "#666").set("font-size", "14px");

        layout.add(icon, text);
        return layout;
    }

    private static HorizontalLayout createDatesRow(Reservation reservation) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.getStyle().set("gap", "5px").set("margin-bottom", "8px");

        Icon icon = VaadinIcon.CALENDAR.create();
        icon.setSize("14px");
        icon.getStyle().set("color", "#667eea");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        long nights = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());

        Span text = new Span(
                reservation.getStartDate().format(formatter) + " - " +
                        reservation.getEndDate().format(formatter) + " (" + nights + " " +
                        (nights == 1 ? "noc" : nights < 5 ? "noce" : "nocy") + ")"
        );
        text.getStyle().set("color", "#666").set("font-size", "14px");

        layout.add(icon, text);
        return layout;
    }

    private static HorizontalLayout createClientRow(Reservation reservation, boolean showingMyReservations) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.getStyle().set("gap", "5px").set("margin-bottom", "8px");

        Icon icon = VaadinIcon.USER.create();
        icon.setSize("14px");
        icon.getStyle().set("color", "#667eea");

        String clientName = showingMyReservations ?
                "Wynajmujący: " + reservation.getApartment().getLandlord().getFirstName() + " " +
                        reservation.getApartment().getLandlord().getLastName() :
                "Najemca: " + reservation.getUser().getFirstName() + " " +
                        reservation.getUser().getLastName();

        Span text = new Span(clientName);
        text.getStyle().set("color", "#666").set("font-size", "14px");

        layout.add(icon, text);
        return layout;
    }

    private static Span createReservationNumberRow(Reservation reservation) {
        Span number = new Span("Rezerwacja: " + reservation.getReservationNumber());
        number.getStyle()
                .set("color", "#999")
                .set("font-size", "12px")
                .set("font-family", "monospace");
        return number;
    }

    private static VerticalLayout createPriceSection(Reservation reservation, boolean showingMyReservations) {
        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setSpacing(false);
        rightColumn.setPadding(false);
        rightColumn.setAlignItems(VerticalLayout.Alignment.END);
        rightColumn.getStyle().set("gap", "10px");

        Span statusBadge = createStatusBadge(reservation.getStatus());

        H3 price = new H3(String.format("%.2f PLN", reservation.getTotalPrice()));
        price.getStyle()
                .set("margin", "0")
                .set("color", "#667eea")
                .set("font-size", "24px");

        String priceLabel = showingMyReservations ? "Zapłacono" : "Przychód";
        Span priceLabelSpan = new Span(priceLabel);
        priceLabelSpan.getStyle()
                .set("color", "#999")
                .set("font-size", "13px");

        rightColumn.add(statusBadge, price, priceLabelSpan);
        return rightColumn;
    }

    private static Span createStatusBadge(ReservationStatus status) {
        Span badge = new Span(status.getDisplayName());
        badge.getStyle()
                .set("padding", "6px 12px")
                .set("border-radius", "20px")
                .set("font-size", "13px")
                .set("font-weight", "500");

        switch (status) {
            case PENDING:
                badge.getStyle()
                        .set("background-color", "#fff3cd")
                        .set("color", "#856404");
                break;
            case CONFIRMED:
                badge.getStyle()
                        .set("background-color", "#d1ecf1")
                        .set("color", "#0c5460");
                break;
            case CANCELLED:
                badge.getStyle()
                        .set("background-color", "#f8d7da")
                        .set("color", "#721c24");
                break;
            case COMPLETED:
                badge.getStyle()
                        .set("background-color", "#d4edda")
                        .set("color", "#155724");
                break;
        }

        return badge;
    }
}