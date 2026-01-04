package com.example.application.views.reservation;

import com.example.application.entity.Reservation;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ReservationService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Route(value = "reservation-confirmation", layout = MainLayout.class)
@PageTitle("Potwierdzenie rezerwacji")
@PermitAll
public class ReservationConfirmationView extends Div implements HasUrlParameter<String> {

    private final ReservationService reservationService;
    private Reservation reservation;
    private final User currentUser;

    public ReservationConfirmationView(ReservationService reservationService,
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

        if (reservation == null || !reservation.getUser().getId().equals(currentUser.getId())) {
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
        mainLayout.setMaxWidth("900px");
        mainLayout.setWidth("100%");
        mainLayout.getStyle()
                .set("margin", "0 auto")
                .set("padding", "30px 20px");

        Div successHeader = createSuccessHeader();
        mainLayout.add(successHeader);

        Div reservationDetails = createReservationDetails();
        mainLayout.add(reservationDetails);

        Div apartmentDetails = createApartmentDetails();
        mainLayout.add(apartmentDetails);

        Div landlordContact = createLandlordContact();
        mainLayout.add(landlordContact);

        Div paymentInfo = createPaymentInfo();
        mainLayout.add(paymentInfo);

        Div importantInfo = createImportantInfo();
        mainLayout.add(importantInfo);

        HorizontalLayout actions = createActions();
        mainLayout.add(actions);

        add(mainLayout);
    }

    private Div createSuccessHeader() {
        Div header = new Div();
        header.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border-radius", "12px")
                .set("padding", "40px")
                .set("text-align", "center")
                .set("margin-bottom", "30px")
                .set("color", "white");

        Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
        checkIcon.setSize("64px");
        checkIcon.getStyle()
                .set("color", "white")
                .set("margin-bottom", "15px");

        H1 title = new H1("Rezerwacja potwierdzona!");
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "white")
                .set("font-size", "32px");

        Paragraph subtitle = new Paragraph("Twoja rezerwacja została przyjęta. Szczegóły zostały wysłane na adres e-mail.");
        subtitle.getStyle()
                .set("margin", "0")
                .set("color", "rgba(255,255,255,0.9)")
                .set("font-size", "16px");

        header.add(checkIcon, title, subtitle);
        return header;
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
        long nights = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());

        HorizontalLayout reservationNumber = createInfoRow(
                VaadinIcon.BARCODE,
                "Numer rezerwacji",
                reservation.getReservationNumber()  // Zamiast UUID
        );

        HorizontalLayout status = createInfoRow(
                VaadinIcon.INFO_CIRCLE,
                "Status",
                reservation.getStatus().getDisplayName()
        );

        HorizontalLayout dates = createInfoRow(
                VaadinIcon.CALENDAR,
                "Termin pobytu",
                reservation.getStartDate().format(formatter) + " - " +
                        reservation.getEndDate().format(formatter) + " (" + nights + " " +
                        (nights == 1 ? "noc" : nights < 5 ? "noce" : "nocy") + ")"
        );

        HorizontalLayout guests = createInfoRow(
                VaadinIcon.USERS,
                "Liczba gości",
                reservation.getGuestsNumber().toString()
        );

        HorizontalLayout created = createInfoRow(
                VaadinIcon.CLOCK,
                "Data rezerwacji",
                reservation.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        );

        if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
            Div notesSection = new Div();
            notesSection.getStyle()
                    .set("margin-top", "20px")
                    .set("padding", "15px")
                    .set("background-color", "#f8f9fa")
                    .set("border-radius", "8px")
                    .set("border-left", "4px solid #667eea");

            Span notesLabel = new Span("Twoje uwagi:");
            notesLabel.getStyle()
                    .set("font-weight", "600")
                    .set("color", "#333")
                    .set("display", "block")
                    .set("margin-bottom", "8px");

            Paragraph notesText = new Paragraph(reservation.getNotes());
            notesText.getStyle()
                    .set("margin", "0")
                    .set("color", "#666")
                    .set("line-height", "1.6");

            notesSection.add(notesLabel, notesText);
            card.add(title, reservationNumber, status, dates, guests, created, notesSection);
        } else {
            card.add(title, reservationNumber, status, dates, guests, created);
        }

        return card;
    }

    private Div createApartmentDetails() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Apartament");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        HorizontalLayout apartmentHeader = new HorizontalLayout();
        apartmentHeader.setWidth("100%");
        apartmentHeader.setAlignItems(HorizontalLayout.Alignment.CENTER);
        apartmentHeader.getStyle()
                .set("gap", "20px")
                .set("margin-bottom", "20px");

        if (reservation.getApartment().getMainImage() != null) {
            Image thumbnail = new Image();
            StreamResource resource = new StreamResource("apartment-thumb",
                    () -> new ByteArrayInputStream(reservation.getApartment().getMainImage().getImageData()));
            thumbnail.setSrc(resource);
            thumbnail.setWidth("120px");
            thumbnail.setHeight("120px");
            thumbnail.getStyle()
                    .set("border-radius", "8px")
                    .set("object-fit", "cover");
            apartmentHeader.add(thumbnail);
        }

        VerticalLayout apartmentInfo = new VerticalLayout();
        apartmentInfo.setSpacing(false);
        apartmentInfo.setPadding(false);

        H4 apartmentName = new H4(reservation.getApartment().getTitle());
        apartmentName.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#333");

        HorizontalLayout features = new HorizontalLayout();
        features.getStyle().set("gap", "15px");

        Span rooms = new Span(VaadinIcon.BED.create(), new Span(" " + reservation.getApartment().getRoomsNumber() + " pokoje"));
        rooms.getStyle().set("color", "#666").set("font-size", "14px");

        Span maxGuests = new Span(VaadinIcon.USERS.create(), new Span(" do " + reservation.getApartment().getMaxPerson() + " osób"));
        maxGuests.getStyle().set("color", "#666").set("font-size", "14px");

        features.add(rooms, maxGuests);

        apartmentInfo.add(apartmentName, features);
        apartmentHeader.add(apartmentInfo);

        Div addressSection = new Div();
        addressSection.getStyle()
                .set("background-color", "#e3f2fd")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("border", "2px solid #2196f3")
                .set("margin-bottom", "15px");

        Span addressTitle = new Span("📍 Pełny adres apartamentu:");
        addressTitle.getStyle()
                .set("font-weight", "600")
                .set("color", "#1976d2")
                .set("display", "block")
                .set("margin-bottom", "10px")
                .set("font-size", "16px");

        String fullAddress = reservation.getApartment().getAddress().getStreet() + "\n" +
                reservation.getApartment().getAddress().getZipCode() + " " +
                reservation.getApartment().getAddress().getCity() + "\n" +
                reservation.getApartment().getAddress().getCountry();

        Paragraph addressText = new Paragraph(fullAddress);
        addressText.getStyle()
                .set("margin", "0")
                .set("color", "#1565c0")
                .set("font-size", "15px")
                .set("line-height", "1.8")
                .set("white-space", "pre-line");

        addressSection.add(addressTitle, addressText);

        card.add(title, apartmentHeader, addressSection);
        return card;
    }

    private Div createLandlordContact() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Kontakt z wynajmującym");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        User landlord = reservation.getApartment().getLandlord();

        HorizontalLayout name = createInfoRow(
                VaadinIcon.USER,
                "Wynajmujący",
                landlord.getFirstName() + " " + landlord.getLastName()
        );

        HorizontalLayout email = new HorizontalLayout();
        email.setWidth("100%");
        email.setAlignItems(HorizontalLayout.Alignment.CENTER);
        email.getStyle().set("gap", "15px").set("margin-bottom", "12px");

        Icon emailIcon = VaadinIcon.ENVELOPE.create();
        emailIcon.setSize("20px");
        emailIcon.getStyle().set("color", "#667eea");

        VerticalLayout emailInfo = new VerticalLayout();
        emailInfo.setSpacing(false);
        emailInfo.setPadding(false);
        emailInfo.getStyle().set("flex", "1");

        Span emailLabel = new Span("E-mail");
        emailLabel.getStyle()
                .set("font-size", "13px")
                .set("color", "#999");

        Anchor emailLink = new Anchor("mailto:" + landlord.getEmail(), landlord.getEmail());
        emailLink.getStyle()
                .set("font-weight", "500")
                .set("color", "#667eea")
                .set("text-decoration", "none");

        emailInfo.add(emailLabel, emailLink);
        email.add(emailIcon, emailInfo);

        HorizontalLayout phone = new HorizontalLayout();
        phone.setWidth("100%");
        phone.setAlignItems(HorizontalLayout.Alignment.CENTER);
        phone.getStyle().set("gap", "15px").set("margin-bottom", "12px");

        Icon phoneIcon = VaadinIcon.PHONE.create();
        phoneIcon.setSize("20px");
        phoneIcon.getStyle().set("color", "#667eea");

        VerticalLayout phoneInfo = new VerticalLayout();
        phoneInfo.setSpacing(false);
        phoneInfo.setPadding(false);
        phoneInfo.getStyle().set("flex", "1");

        Span phoneLabel = new Span("Telefon");
        phoneLabel.getStyle()
                .set("font-size", "13px")
                .set("color", "#999");

        Anchor phoneLink = new Anchor("tel:" + landlord.getTelephoneNumber(), landlord.getTelephoneNumber());
        phoneLink.getStyle()
                .set("font-weight", "500")
                .set("color", "#667eea")
                .set("text-decoration", "none");

        phoneInfo.add(phoneLabel, phoneLink);
        phone.add(phoneIcon, phoneInfo);

        if (landlord.getCompany() != null) {
            HorizontalLayout company = createInfoRow(
                    VaadinIcon.BUILDING,
                    "Firma",
                    landlord.getCompany().getCompanyName()
            );

            HorizontalLayout nip = createInfoRow(
                    VaadinIcon.CLIPBOARD_TEXT,
                    "NIP",
                    landlord.getCompany().getNip()
            );

            card.add(title, name, email, phone, company, nip);
        } else {
            card.add(title, name, email, phone);
        }

        return card;
    }

    private Div createPaymentInfo() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Informacje o płatności");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        HorizontalLayout paymentMethod = createInfoRow(
                VaadinIcon.CREDIT_CARD,
                "Metoda płatności",
                reservation.getPaymentMethod().getMaskedCardNumber()
        );

        HorizontalLayout totalPrice = new HorizontalLayout();
        totalPrice.setWidth("100%");
        totalPrice.setAlignItems(HorizontalLayout.Alignment.CENTER);
        totalPrice.getStyle()
                .set("gap", "15px")
                .set("padding", "15px")
                .set("background-color", "#f8f9fa")
                .set("border-radius", "8px")
                .set("margin-top", "15px");

        Icon priceIcon = VaadinIcon.MONEY.create();
        priceIcon.setSize("24px");
        priceIcon.getStyle().set("color", "#4caf50");

        VerticalLayout priceInfo = new VerticalLayout();
        priceInfo.setSpacing(false);
        priceInfo.setPadding(false);
        priceInfo.getStyle().set("flex", "1");

        Span priceLabel = new Span("Kwota do zapłaty");
        priceLabel.getStyle()
                .set("font-size", "14px")
                .set("color", "#666");

        H2 priceValue = new H2(String.format("%.2f PLN", reservation.getTotalPrice()));
        priceValue.getStyle()
                .set("margin", "5px 0 0 0")
                .set("color", "#4caf50")
                .set("font-size", "28px");

        priceInfo.add(priceLabel, priceValue);
        totalPrice.add(priceIcon, priceInfo);

        Span paymentNote = new Span("✓ Płatność zostanie pobrana z wybranej karty zgodnie z warunkami rezerwacji.");
        paymentNote.getStyle()
                .set("color", "#666")
                .set("font-size", "13px")
                .set("font-style", "italic")
                .set("display", "block")
                .set("margin-top", "10px");

        card.add(title, paymentMethod, totalPrice, paymentNote);
        return card;
    }

    private Div createImportantInfo() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#fff3cd")
                .set("border", "2px solid #ffc107")
                .set("border-radius", "12px")
                .set("padding", "25px")
                .set("margin-bottom", "20px");

        H3 title = new H3("ℹ️ Ważne informacje");
        title.getStyle()
                .set("margin", "0 0 15px 0")
                .set("color", "#856404");

        UnorderedList infoList = new UnorderedList();
        infoList.getStyle()
                .set("margin", "0")
                .set("padding-left", "20px")
                .set("color", "#856404")
                .set("line-height", "1.8");

        infoList.add(
                new ListItem("Szczegóły rezerwacji zostały wysłane na Twój adres e-mail"),
                new ListItem("Skontaktuj się z wynajmującym w celu ustalenia szczegółów zameldowania"),
                new ListItem("W razie pytań lub problemów skontaktuj się z wynajmującym")
        );

        card.add(title, infoList);
        return card;
    }

    private HorizontalLayout createActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidth("100%");
        actions.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        actions.getStyle()
                .set("gap", "15px")
                .set("margin-top", "20px");

        Button myReservationsButton = new Button("Moje rezerwacje", e ->
                UI.getCurrent().navigate("transaction-history"));
        myReservationsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        myReservationsButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        Button backToOffersButton = new Button("Przeglądaj oferty", e ->
                UI.getCurrent().navigate(""));
        backToOffersButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        actions.add(myReservationsButton, backToOffersButton);
        return actions;
    }

    private HorizontalLayout createInfoRow(VaadinIcon iconType, String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth("100%");
        row.setAlignItems(HorizontalLayout.Alignment.CENTER);
        row.getStyle().set("gap", "15px").set("margin-bottom", "12px");

        Icon icon = iconType.create();
        icon.setSize("20px");
        icon.getStyle().set("color", "#667eea");

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);
        info.getStyle().set("flex", "1");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "13px")
                .set("color", "#999");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-weight", "500")
                .set("color", "#333");

        info.add(labelSpan, valueSpan);
        row.add(icon, info);

        return row;
    }
}