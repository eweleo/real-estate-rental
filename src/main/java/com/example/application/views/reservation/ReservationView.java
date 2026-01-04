package com.example.application.views.reservation;

import com.example.application.entity.Apartment;
import com.example.application.entity.PaymentMethod;
import com.example.application.entity.Reservation;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentService;
import com.example.application.services.PaymentMethodService;
import com.example.application.services.ReservationService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Route(value = "reservation", layout = MainLayout.class)
@PageTitle("Rezerwacja")
@PermitAll
public class ReservationView extends Div implements HasUrlParameter<String> {

    private final ApartmentService apartmentService;
    private final ReservationService reservationService;
    private final PaymentMethodService paymentMethodService;

    private Apartment apartment;
    private final User currentUser;

    private TextField firstName;
    private TextField lastName;
    private TextField phoneNumber;

    private DatePicker startDate;
    private DatePicker endDate;
    private IntegerField guestsNumber;
    private TextArea notes;

    private Span totalPriceSpan;
    private Span nightsCountSpan;
    private Div paymentCardInfo;
    private PaymentMethod selectedPaymentMethod;

    public ReservationView(ApartmentService apartmentService,
                           ReservationService reservationService,
                           PaymentMethodService paymentMethodService,
                           AuthenticatedUser authenticatedUser) {
        this.apartmentService = apartmentService;
        this.reservationService = reservationService;
        this.paymentMethodService = paymentMethodService;
        this.currentUser = authenticatedUser.get().orElse(null);
    }

    @Override
    public void setParameter(BeforeEvent event, String uuid) {
        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        apartment = apartmentService.findByUuid(uuid);

        if (apartment == null) {
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
        mainLayout.setMaxWidth("1000px");
        mainLayout.setWidth("100%");
        mainLayout.getStyle()
                .set("margin", "0 auto")
                .set("padding", "30px 20px");

        Button backButton = new Button("Powrót do oferty", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate("apartment/" + apartment.getUuid()));

        H1 title = new H1("Rezerwacja apartamentu");
        title.getStyle()
                .set("margin", "20px 0")
                .set("color", "#333");

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidth("100%");
        contentLayout.setSpacing(true);
        contentLayout.getStyle()
                .set("gap", "30px")
                .set("align-items", "flex-start");

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.getStyle().set("flex", "2");
        leftColumn.add(createApartmentSummary());
        leftColumn.add(createTenantDataForm());
        leftColumn.add(createReservationForm());

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.getStyle().set("flex", "1");
        rightColumn.add(createPriceSummary());
        rightColumn.add(createPaymentMethodSection());

        contentLayout.add(leftColumn, rightColumn);

        mainLayout.add(backButton, title, contentLayout);
        add(mainLayout);
    }

    private Div createApartmentSummary() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "20px")
                .set("margin-bottom", "20px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        layout.getStyle().set("gap", "20px");

        if (apartment.getMainImage() != null) {
            Image thumbnail = new Image();
            StreamResource resource = new StreamResource("thumb",
                    () -> new ByteArrayInputStream(apartment.getMainImage().getImageData()));
            thumbnail.setSrc(resource);
            thumbnail.setWidth("120px");
            thumbnail.setHeight("120px");
            thumbnail.getStyle()
                    .set("border-radius", "8px")
                    .set("object-fit", "cover");
            layout.add(thumbnail);
        }

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        H3 apartmentTitle = new H3(apartment.getTitle());
        apartmentTitle.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#333");

        HorizontalLayout locationLayout = new HorizontalLayout();
        locationLayout.getStyle().set("gap", "5px");
        Icon locationIcon = VaadinIcon.MAP_MARKER.create();
        locationIcon.setSize("16px");
        locationIcon.getStyle().set("color", "#667eea");
        Span location = new Span(apartment.getAddress().getCity());
        location.getStyle().set("color", "#666");
        locationLayout.add(locationIcon, location);

        HorizontalLayout detailsLayout = new HorizontalLayout();
        detailsLayout.getStyle().set("gap", "15px").set("margin-top", "10px");

        Span rooms = new Span(apartment.getRoomsNumber() + " pokoje");
        rooms.getStyle().set("color", "#666").set("font-size", "14px");

        Span guests = new Span("do " + apartment.getMaxPerson() + " osób");
        guests.getStyle().set("color", "#666").set("font-size", "14px");

        detailsLayout.add(rooms, guests);

        infoLayout.add(apartmentTitle, locationLayout, detailsLayout);
        layout.add(infoLayout);

        card.add(layout);
        return card;
    }

    private Div createTenantDataForm() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 sectionTitle = new H3("Dane głównego najemcy");
        sectionTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        firstName = new TextField("Imię");
        firstName.setWidth("100%");
        firstName.setRequired(true);
        firstName.setValue(currentUser.getFirstName() != null ? currentUser.getFirstName() : "");

        lastName = new TextField("Nazwisko");
        lastName.setWidth("100%");
        lastName.setRequired(true);
        lastName.setValue(currentUser.getLastName() != null ? currentUser.getLastName() : "");

        phoneNumber = new TextField("Numer telefonu");
        phoneNumber.setWidth("100%");
        phoneNumber.setRequired(true);
        phoneNumber.setPlaceholder("123456789");
        phoneNumber.setValue(currentUser.getTelephoneNumber() != null ? currentUser.getTelephoneNumber() : "");

        HorizontalLayout nameLayout = new HorizontalLayout(firstName, lastName);
        nameLayout.setWidth("100%");
        nameLayout.getStyle().set("gap", "15px");

        card.add(sectionTitle, nameLayout, phoneNumber);
        return card;
    }

    private Div createReservationForm() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 sectionTitle = new H3("Szczegóły rezerwacji");
        sectionTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#667eea");

        startDate = new DatePicker("Data zameldowania");
        startDate.setWidth("100%");
        startDate.setRequired(true);
        startDate.setMin(LocalDate.now().plusDays(1));
        startDate.addValueChangeListener(e -> {
            if (e.getValue() != null && endDate.getValue() != null) {
                if (e.getValue().isAfter(endDate.getValue()) ||
                        e.getValue().isEqual(endDate.getValue())) {
                    endDate.setValue(e.getValue().plusDays(1));
                }
            }
            if (endDate.getValue() != null) {
                endDate.setMin(e.getValue().plusDays(1));
            }
            updatePriceSummary();
        });

        endDate = new DatePicker("Data wymeldowania");
        endDate.setWidth("100%");
        endDate.setRequired(true);
        endDate.setMin(LocalDate.now().plusDays(2));
        endDate.addValueChangeListener(e -> updatePriceSummary());

        HorizontalLayout datesLayout = new HorizontalLayout(startDate, endDate);
        datesLayout.setWidth("100%");
        datesLayout.getStyle().set("gap", "15px");

        guestsNumber = new IntegerField("Liczba gości");
        guestsNumber.setWidth("100%");
        guestsNumber.setRequired(true);
        guestsNumber.setMin(1);
        guestsNumber.setMax(apartment.getMaxPerson());
        guestsNumber.setValue(1);
        guestsNumber.setHelperText("Maksymalnie " + apartment.getMaxPerson() + " osób");

        notes = new TextArea("Uwagi specjalne (opcjonalnie)");
        notes.setWidth("100%");
        notes.setPlaceholder("Np. godzina przyjazdu, dodatkowe życzenia...");
        notes.setMaxLength(500);

        card.add(sectionTitle, datesLayout, guestsNumber, notes);
        return card;
    }

    private Div createPriceSummary() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "25px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Podsumowanie");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#333");

        HorizontalLayout pricePerNightLayout = new HorizontalLayout();
        pricePerNightLayout.setWidth("100%");
        pricePerNightLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        Span priceLabel = new Span("Cena za noc:");
        priceLabel.getStyle().set("color", "#666");

        Span priceValue = new Span(String.format("%.2f PLN", apartment.getPrice()));
        priceValue.getStyle().set("color", "#333").set("font-weight", "500");

        pricePerNightLayout.add(priceLabel, priceValue);

        HorizontalLayout nightsLayout = new HorizontalLayout();
        nightsLayout.setWidth("100%");
        nightsLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        Span nightsLabel = new Span("Liczba nocy:");
        nightsLabel.getStyle().set("color", "#666");

        nightsCountSpan = new Span("-");
        nightsCountSpan.getStyle().set("color", "#333").set("font-weight", "500");

        nightsLayout.add(nightsLabel, nightsCountSpan);

        Hr separator = new Hr();
        separator.getStyle()
                .set("margin", "15px 0")
                .set("border", "none")
                .set("border-top", "2px solid #f0f0f0");

        HorizontalLayout totalLayout = new HorizontalLayout();
        totalLayout.setWidth("100%");
        totalLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        Span totalLabel = new Span("Do zapłaty:");
        totalLabel.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "600")
                .set("color", "#333");

        totalPriceSpan = new Span("0.00 PLN");
        totalPriceSpan.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "#667eea");

        totalLayout.add(totalLabel, totalPriceSpan);

        card.add(title, pricePerNightLayout, nightsLayout, separator, totalLayout);
        return card;
    }

    private Div createPaymentMethodSection() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "25px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Metoda płatności");
        title.getStyle()
                .set("margin", "0 0 15px 0")
                .set("color", "#333");

        paymentCardInfo = new Div();
        updatePaymentMethodDisplay();

        Button changeCardButton = new Button("Zmień kartę", VaadinIcon.CREDIT_CARD.create(), e -> openChangeCardDialog());
        changeCardButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        changeCardButton.setWidth("100%");
        changeCardButton.getStyle()
                .set("margin-top", "10px")
                .set("justify-content", "center");

        Button reserveButton = new Button("Potwierdź rezerwację");
        reserveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        reserveButton.setWidth("100%");
        reserveButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("margin-top", "20px");

        reserveButton.addClickListener(e -> handleReservation());

        card.add(title, paymentCardInfo, changeCardButton, reserveButton);
        return card;
    }

    private void updatePaymentMethodDisplay() {
        paymentCardInfo.removeAll();

        if (selectedPaymentMethod == null) {
            Optional<PaymentMethod> defaultCard = paymentMethodService.getDefaultPaymentMethod(currentUser);
            selectedPaymentMethod = defaultCard.orElse(null);
        }

        if (selectedPaymentMethod != null) {
            Div cardDisplay = new Div();
            cardDisplay.getStyle()
                    .set("background-color", "#f8f9fa")
                    .set("padding", "15px")
                    .set("border-radius", "8px")
                    .set("border", "2px solid #667eea")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("gap", "12px");

            Icon cardIcon = VaadinIcon.CREDIT_CARD.create();
            cardIcon.setSize("28px");
            cardIcon.getStyle().set("color", "#667eea");

            VerticalLayout cardInfo = new VerticalLayout();
            cardInfo.setSpacing(false);
            cardInfo.setPadding(false);
            cardInfo.getStyle().set("flex", "1");

            Span cardNumber = new Span(selectedPaymentMethod.getMaskedCardNumber());
            cardNumber.getStyle()
                    .set("font-weight", "500")
                    .set("font-size", "16px")
                    .set("color", "#333");

            Span cardNote = new Span("✓ Płatność zostanie pobrana z tej karty");
            cardNote.getStyle()
                    .set("font-size", "13px")
                    .set("color", "#4caf50");

            cardInfo.add(cardNumber, cardNote);
            cardDisplay.add(cardIcon, cardInfo);
            paymentCardInfo.add(cardDisplay);
        } else {
            Div noCardWarning = new Div();
            noCardWarning.getStyle()
                    .set("background-color", "#fff3cd")
                    .set("padding", "15px")
                    .set("border-radius", "8px")
                    .set("border", "1px solid #ffc107")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("gap", "10px");

            Icon warningIcon = VaadinIcon.WARNING.create();
            warningIcon.setSize("24px");
            warningIcon.getStyle().set("color", "#856404");

            Span warningText = new Span("Nie masz dodanej karty płatności");
            warningText.getStyle()
                    .set("color", "#856404")
                    .set("font-weight", "500");

            noCardWarning.add(warningIcon, warningText);
            paymentCardInfo.add(noCardWarning);
        }
    }

    private void openChangeCardDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Wybierz kartę płatności");
        dialog.setWidth("550px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        List<PaymentMethod> cards = paymentMethodService.findByUser(currentUser);

        if (cards.isEmpty()) {
            Div emptyState = new Div();
            emptyState.getStyle()
                    .set("padding", "40px")
                    .set("text-align", "center");

            Icon emptyIcon = VaadinIcon.CREDIT_CARD.create();
            emptyIcon.setSize("48px");
            emptyIcon.getStyle()
                    .set("color", "#ccc")
                    .set("margin-bottom", "15px");

            Span noCardsText = new Span("Nie masz dodanych kart płatności");
            noCardsText.getStyle()
                    .set("color", "#666")
                    .set("font-size", "16px")
                    .set("display", "block")
                    .set("margin-bottom", "20px");

            Button addFirstCard = new Button("Dodaj pierwszą kartę", VaadinIcon.PLUS.create(), e -> {
                dialog.close();
                UI.getCurrent().navigate("payment-methods");
            });
            addFirstCard.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            emptyState.add(emptyIcon, noCardsText, addFirstCard);
            layout.add(emptyState);
        } else {
            VerticalLayout cardsContainer = new VerticalLayout();
            cardsContainer.setPadding(false);
            cardsContainer.setSpacing(false);
            cardsContainer.getStyle()
                    .set("gap", "10px")
                    .set("padding", "20px");

            for (PaymentMethod card : cards) {
                Div cardOption = createCardOption(card);
                cardOption.addClickListener(e -> {
                    selectedPaymentMethod = card;
                    updatePaymentMethodDisplay();
                    dialog.close();
                    showNotification("Karta została zmieniona", NotificationVariant.LUMO_SUCCESS);
                });
                cardsContainer.add(cardOption);
            }

            layout.add(cardsContainer);

            Hr separator = new Hr();
            separator.getStyle()
                    .set("margin", "0")
                    .set("border", "none")
                    .set("border-top", "1px solid #e0e0e0");

            Div addNewSection = new Div();
            addNewSection.getStyle()
                    .set("padding", "15px 20px")
                    .set("cursor", "pointer")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("gap", "10px");

            addNewSection.getElement().addEventListener("mouseenter", evt -> addNewSection.getStyle().set("background-color", "#f5f5f5"));

            addNewSection.getElement().addEventListener("mouseleave", evt -> addNewSection.getStyle().set("background-color", "transparent"));

            Icon plusIcon = VaadinIcon.PLUS_CIRCLE.create();
            plusIcon.setSize("24px");
            plusIcon.getStyle().set("color", "#667eea");

            Span addNewText = new Span("Dodaj nową kartę");
            addNewText.getStyle()
                    .set("color", "#667eea")
                    .set("font-weight", "500");

            addNewSection.add(plusIcon, addNewText);
            addNewSection.addClickListener(e -> {
                dialog.close();
                UI.getCurrent().navigate("payment-methods");
            });

            layout.add(separator, addNewSection);
        }

        Button cancelButton = new Button("Anuluj", e -> dialog.close());
        dialog.getFooter().add(cancelButton);

        dialog.add(layout);
        dialog.open();
    }

    private Div createCardOption(PaymentMethod card) {
        boolean isSelected = selectedPaymentMethod != null &&
                selectedPaymentMethod.getId().equals(card.getId());

        Div cardOption = new Div();
        cardOption.getStyle()
                .set("padding", "15px")
                .set("border-radius", "8px")
                .set("border", isSelected ? "2px solid #667eea" : "2px solid #e0e0e0")
                .set("cursor", "pointer")
                .set("background-color", isSelected ? "#f0f4ff" : "#ffffff")
                .set("transition", "all 0.2s")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px");

        cardOption.getElement().addEventListener("mouseenter", evt -> {
            if (!isSelected) {
                cardOption.getStyle()
                        .set("border-color", "#667eea")
                        .set("background-color", "#fafafa");
            }
        });

        cardOption.getElement().addEventListener("mouseleave", evt -> {
            if (!isSelected) {
                cardOption.getStyle()
                        .set("border-color", "#e0e0e0")
                        .set("background-color", "#ffffff");
            }
        });

        Icon cardIcon = VaadinIcon.CREDIT_CARD.create();
        cardIcon.setSize("28px");
        cardIcon.getStyle().set("color", isSelected ? "#667eea" : "#999");

        VerticalLayout cardInfo = new VerticalLayout();
        cardInfo.setSpacing(false);
        cardInfo.setPadding(false);
        cardInfo.getStyle().set("flex", "1");

        Span cardNumber = new Span(card.getMaskedCardNumber());
        cardNumber.getStyle()
                .set("font-weight", "500")
                .set("font-size", "15px")
                .set("color", "#333");

        if (card.isDefault()) {
            HorizontalLayout numberWithBadge = new HorizontalLayout();
            numberWithBadge.setSpacing(false);
            numberWithBadge.setPadding(false);
            numberWithBadge.setAlignItems(HorizontalLayout.Alignment.CENTER);
            numberWithBadge.getStyle().set("gap", "8px");

            Span defaultBadge = new Span("Domyślna");
            defaultBadge.getStyle()
                    .set("background-color", "#4caf50")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px")
                    .set("font-size", "11px")
                    .set("font-weight", "600");

            numberWithBadge.add(cardNumber, defaultBadge);
            cardInfo.add(numberWithBadge);
        } else {
            cardInfo.add(cardNumber);
        }

        if (isSelected) {
            Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
            checkIcon.setSize("24px");
            checkIcon.getStyle().set("color", "#667eea");
            cardOption.add(cardIcon, cardInfo, checkIcon);
        } else {
            cardOption.add(cardIcon, cardInfo);
        }

        return cardOption;
    }

    private void updatePriceSummary() {
        if (startDate.getValue() != null && endDate.getValue() != null) {
            long nights = ChronoUnit.DAYS.between(startDate.getValue(), endDate.getValue());

            if (nights > 0) {
                nightsCountSpan.setText(nights + " " + (nights == 1 ? "noc" : "nocy"));
                double totalPrice = apartment.getPrice() * nights;
                totalPriceSpan.setText(String.format("%.2f PLN", totalPrice));
            } else {
                nightsCountSpan.setText("-");
                totalPriceSpan.setText("0.00 PLN");
            }
        } else {
            nightsCountSpan.setText("-");
            totalPriceSpan.setText("0.00 PLN");
        }
    }

    private void handleReservation() {
        if (!validateReservation()) {
            return;
        }

        if (selectedPaymentMethod == null) {
            showNotification("Wybierz kartę płatności", NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            long nights = ChronoUnit.DAYS.between(startDate.getValue(), endDate.getValue());
            Reservation reservation = getReservation(nights);

            reservationService.createReservation(reservation);

            showNotification("Rezerwacja została utworzona pomyślnie!", NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("reservation-confirmation/" + reservation.getUuid());

        } catch (IllegalArgumentException e) {
            showNotification(e.getMessage(), NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Wystąpił błąd: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private Reservation getReservation(long nights) {
        double totalPrice = apartment.getPrice() * nights;


        Reservation reservation = new Reservation();
        reservation.setApartment(apartment);
        reservation.setUser(currentUser);
        reservation.setStartDate(startDate.getValue());
        reservation.setEndDate(endDate.getValue());
        reservation.setGuestsNumber(guestsNumber.getValue());
        reservation.setTotalPrice(totalPrice);
        reservation.setNotes(notes.getValue());
        reservation.setPaymentMethod(selectedPaymentMethod);
        return reservation;
    }

    private boolean validateReservation() {
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            showNotification("Wypełnij dane najemcy", NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (!phoneNumber.getValue().matches("\\d{9}")) {
            showNotification("Numer telefonu musi składać się z 9 cyfr", NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (startDate.isEmpty() || endDate.isEmpty()) {
            showNotification("Wybierz daty pobytu", NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (startDate.getValue().isBefore(LocalDate.now())) {
            showNotification("Data zameldowania nie może być w przeszłości", NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (endDate.getValue().isBefore(startDate.getValue()) ||
                endDate.getValue().isEqual(startDate.getValue())) {
            showNotification("Data wymeldowania musi być po dacie zameldowania", NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (guestsNumber.isEmpty() || guestsNumber.getValue() < 1) {
            showNotification("Podaj liczbę gości", NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (guestsNumber.getValue() > apartment.getMaxPerson()) {
            showNotification("Liczba gości przekracza maksymalną liczbę osób", NotificationVariant.LUMO_ERROR);
            return false;
        }

        return true;
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(4000);
    }
}