package com.example.application.views.transaction;

import com.example.application.entity.Reservation;
import com.example.application.entity.ReservationStatus;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ReservationService;
import com.example.application.views.account.UserLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Route(value = "landlord-reservation-details", layout = UserLayout.class)
@PageTitle("Szczegóły rezerwacji")
@RolesAllowed("LANDLORD")
public class LandlordReservationDetailsView extends Div implements HasUrlParameter<String> {

    private final ReservationService reservationService;
    private Reservation reservation;
    private final User currentUser;

    public LandlordReservationDetailsView(ReservationService reservationService,
                                          AuthenticatedUser authenticatedUser) {
        this.reservationService = reservationService;
        this.currentUser = authenticatedUser.get().orElse(null);
    }

    @Override
    public void setParameter(BeforeEvent event, String uuid) {
        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        reservation = reservationService.findByUuid(uuid).orElse(null);

        if (reservation == null || !reservation.getApartment().getLandlord().getId().equals(currentUser.getId())) {
            UI.getCurrent().navigate("");
            return;
        }

        configureView();
    }

    private void configureView() {
        setSizeFull();
        getStyle()
                .set("background-color", "#f5f5f5")
                .set("padding", "0");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMaxWidth("1200px");
        mainLayout.setWidth("100%");
        mainLayout.getStyle()
                .set("margin", "0 auto")
                .set("padding", "30px 20px");

        Button backButton = new Button("Powrót do historii", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate("transaction-history"));

        H1 title = new H1("Szczegóły rezerwacji");
        title.getStyle()
                .set("margin", "20px 0")
                .set("color", "#333");

        mainLayout.add(backButton, title);
        mainLayout.add(createStatusAndActionsSection());

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidth("100%");
        contentLayout.setSpacing(true);
        contentLayout.getStyle()
                .set("gap", "20px")
                .set("align-items", "flex-start");

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.getStyle().set("flex", "2");
        leftColumn.add(createReservationDetails());
        leftColumn.add(createTenantInfo());

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.getStyle().set("flex", "1");
        rightColumn.add(createApartmentCard());
        rightColumn.add(createPaymentCard());

        contentLayout.add(leftColumn, rightColumn);
        mainLayout.add(contentLayout);

        add(mainLayout);
    }

    private Div createStatusAndActionsSection() {
        Div section = new Div();
        section.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        layout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        VerticalLayout statusLayout = new VerticalLayout();
        statusLayout.setSpacing(false);
        statusLayout.setPadding(false);

        Span statusLabel = new Span("Status rezerwacji");
        statusLabel.getStyle()
                .set("font-size", "14px")
                .set("color", "#999")
                .set("display", "block")
                .set("margin-bottom", "10px");

        Span statusBadge = createLargeStatusBadge(reservation.getStatus());

        Span statusDescription = new Span(getStatusDescription(reservation.getStatus()));
        statusDescription.getStyle()
                .set("display", "block")
                .set("margin-top", "10px")
                .set("color", "#666")
                .set("font-size", "13px")
                .set("max-width", "400px")
                .set("line-height", "1.5");

        statusLayout.add(statusLabel, statusBadge, statusDescription);

        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.getStyle().set("gap", "10px");

        if (reservation.getStatus() == ReservationStatus.PENDING) {
            Button confirmButton = new Button("Potwierdź", VaadinIcon.CHECK.create());
            confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            confirmButton.addClickListener(e -> confirmReservation());
            actionsLayout.add(confirmButton);
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            Button completeButton = new Button("Zakończ", VaadinIcon.CHECK_CIRCLE.create());
            completeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            completeButton.getStyle()
                    .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                    .set("border", "none");
            completeButton.addClickListener(e -> completeReservation());
            actionsLayout.add(completeButton);
        }

        if (reservation.getStatus() == ReservationStatus.PENDING ||
                reservation.getStatus() == ReservationStatus.CONFIRMED) {
            Button cancelButton = new Button("Anuluj", VaadinIcon.CLOSE_CIRCLE.create());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            cancelButton.addClickListener(e -> openCancelDialog());
            actionsLayout.add(cancelButton);
        }

        layout.add(statusLayout, actionsLayout);
        section.add(layout);

        return section;
    }

    private Span createLargeStatusBadge(ReservationStatus status) {
        Span badge = new Span(status.getDisplayName());
        badge.getStyle()
                .set("padding", "8px 16px")
                .set("border-radius", "20px")
                .set("font-size", "16px")
                .set("font-weight", "600")
                .set("display", "inline-block");

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

    private String getStatusDescription(ReservationStatus status) {
        return switch (status) {
            case PENDING -> "Nowa rezerwacja oczekuje na akceptację";
            case CONFIRMED -> "Rezerwacja potwierdzona";
            case CANCELLED -> "Rezerwacja anulowana";
            case COMPLETED -> "Rezerwacja zakończona";
        };
    }

    private Div createReservationDetails() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Szczegóły rezerwacji");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        long nights = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setSpacing(false);
        detailsLayout.setPadding(false);
        detailsLayout.getStyle().set("gap", "15px");

        detailsLayout.add(
                createDetailRow("Numer rezerwacji", reservation.getReservationNumber()),  // Zamiast UUID
                createDetailRow("Termin pobytu",
                        reservation.getStartDate().format(formatter) + " - " +
                                reservation.getEndDate().format(formatter) + " (" + nights + " " +
                                (nights == 1 ? "noc" : nights < 5 ? "noce" : "nocy") + ")"),
                createDetailRow("Liczba gości", reservation.getGuestsNumber().toString() + " " +
                        (reservation.getGuestsNumber() == 1 ? "osoba" : "osoby")),
                createDetailRow("Data utworzenia", reservation.getCreatedAt().format(timeFormatter))
        );

        card.add(title, detailsLayout);

        if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
            Div notesSection = new Div();
            notesSection.getStyle()
                    .set("margin-top", "20px")
                    .set("padding", "15px")
                    .set("background-color", "#f8f9fa")
                    .set("border-radius", "8px")
                    .set("border-left", "4px solid #667eea");

            Span notesLabel = new Span("Uwagi od najemcy:");
            notesLabel.getStyle()
                    .set("font-weight", "600")
                    .set("color", "#333")
                    .set("font-size", "14px")
                    .set("display", "block")
                    .set("margin-bottom", "8px");

            Paragraph notesText = new Paragraph(reservation.getNotes());
            notesText.getStyle()
                    .set("margin", "0")
                    .set("color", "#666")
                    .set("font-size", "14px")
                    .set("line-height", "1.6");

            notesSection.add(notesLabel, notesText);
            card.add(notesSection);
        }

        return card;
    }

    private Div createTenantInfo() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Dane najemcy");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        User tenant = reservation.getUser();

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setSpacing(false);
        detailsLayout.setPadding(false);
        detailsLayout.getStyle().set("gap", "15px");

        detailsLayout.add(
                createDetailRow("Imię i nazwisko", tenant.getFirstName() + " " + tenant.getLastName()),
                createContactRow("E-mail", tenant.getEmail(), "mailto:" + tenant.getEmail()),
                createContactRow("Telefon", tenant.getTelephoneNumber(), "tel:" + tenant.getTelephoneNumber())
        );

        card.add(title, detailsLayout);
        return card;
    }

    private Div createApartmentCard() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "20px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Apartament");
        title.getStyle()
                .set("margin", "0 0 15px 0")
                .set("color", "#667eea")
                .set("font-size", "16px");

        if (reservation.getApartment().getMainImage() != null) {
            Image thumbnail = new Image();
            StreamResource resource = new StreamResource("apartment-thumb",
                    () -> new ByteArrayInputStream(reservation.getApartment().getMainImage().getImageData()));
            thumbnail.setSrc(resource);
            thumbnail.setWidth("100%");
            thumbnail.setHeight("150px");
            thumbnail.getStyle()
                    .set("border-radius", "8px")
                    .set("object-fit", "cover")
                    .set("margin-bottom", "15px");
            card.add(title, thumbnail);
        } else {
            card.add(title);
        }

        H4 apartmentName = new H4(reservation.getApartment().getTitle());
        apartmentName.getStyle()
                .set("margin", "0 0 8px 0")
                .set("color", "#333")
                .set("font-size", "16px");

        HorizontalLayout locationLayout = new HorizontalLayout();
        locationLayout.setSpacing(false);
        locationLayout.getStyle().set("gap", "5px");

        Icon locationIcon = VaadinIcon.MAP_MARKER.create();
        locationIcon.setSize("14px");
        locationIcon.getStyle().set("color", "#667eea");

        Span location = new Span(reservation.getApartment().getAddress().getCity());
        location.getStyle().set("color", "#666").set("font-size", "14px");

        locationLayout.add(locationIcon, location);

        Button viewButton = new Button("Zobacz apartament", e ->
                UI.getCurrent().navigate("apartment/" + reservation.getApartment().getUuid()));
        viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewButton.setWidth("100%");
        viewButton.getStyle().set("margin-top", "10px");

        card.add(apartmentName, locationLayout, viewButton);
        return card;
    }

    private Div createPaymentCard() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "25px")
                .set("text-align", "center");

        Span label = new Span("Kwota");
        label.getStyle()
                .set("display", "block")
                .set("color", "#999")
                .set("font-size", "14px")
                .set("margin-bottom", "10px");

        H2 price = new H2(String.format("%.2f PLN", reservation.getTotalPrice()));
        price.getStyle()
                .set("margin", "0")
                .set("color", "#4caf50")
                .set("font-size", "32px")
                .set("font-weight", "bold");

        Span sublabel = new Span("do otrzymania");
        sublabel.getStyle()
                .set("display", "block")
                .set("color", "#999")
                .set("font-size", "13px")
                .set("margin-top", "5px");

        card.add(label, price, sublabel);
        return card;
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth("100%");
        row.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("color", "#999")
                .set("font-size", "14px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("color", "#333")
                .set("font-weight", "500")
                .set("font-size", "14px");

        row.add(labelSpan, valueSpan);
        return row;
    }

    private HorizontalLayout createContactRow(String label, String value, String href) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth("100%");
        row.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("color", "#999")
                .set("font-size", "14px");

        Anchor link = new Anchor(href, value);
        link.getStyle()
                .set("color", "#667eea")
                .set("font-weight", "500")
                .set("font-size", "14px")
                .set("text-decoration", "none");

        row.add(labelSpan, link);
        return row;
    }

    private void confirmReservation() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        reservationService.update(reservation);

        showNotification("Rezerwacja została potwierdzona");
        UI.getCurrent().getPage().reload();
    }

    private void completeReservation() {
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationService.update(reservation);

        showNotification("Rezerwacja została zakończona");
        UI.getCurrent().getPage().reload();
    }

    private void openCancelDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Anuluj rezerwację");
        dialog.setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        Paragraph warning = new Paragraph("Czy na pewno chcesz anulować tę rezerwację?");
        warning.getStyle()
                .set("color", "#666")
                .set("margin", "0 0 15px 0");

        TextArea reasonField = new TextArea("Powód anulowania (opcjonalnie)");
        reasonField.setWidth("100%");
        reasonField.setPlaceholder("Podaj powód...");
        reasonField.setMaxLength(1000);

        layout.add(warning, reasonField);

        Button cancelConfirmButton = new Button("Anuluj rezerwację", e -> {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservation.setCancelledAt(LocalDateTime.now());
            reservation.setCancellationReason(reasonField.getValue());
            reservationService.update(reservation);

            dialog.close();
            showNotification("Rezerwacja została anulowana");
            UI.getCurrent().getPage().reload();
        });
        cancelConfirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button closeButton = new Button("Zamknij", e -> dialog.close());

        dialog.getFooter().add(closeButton, cancelConfirmButton);
        dialog.add(layout);
        dialog.open();
    }

    private void showNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(4000);
    }
}
