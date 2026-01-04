package com.example.application.views.account;

import com.example.application.entity.Address;
import com.example.application.entity.CardBrand;
import com.example.application.entity.PaymentMethod;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.PaymentMethodService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "payment-methods", layout = UserLayout.class)
@PageTitle("Metody Płatności")
@PermitAll
public class PaymentMethodsView extends Div {

    private final PaymentMethodService paymentMethodService;
    private final AuthenticatedUser authenticatedUser;
    private final User currentUser;

    private VerticalLayout cardsContainer;

    public PaymentMethodsView(PaymentMethodService paymentMethodService, AuthenticatedUser authenticatedUser) {
        this.paymentMethodService = paymentMethodService;
        this.authenticatedUser = authenticatedUser;
        this.currentUser = authenticatedUser.get().orElseThrow();

        configureView();
        loadPaymentMethods();
    }

    private void configureView() {
        setSizeFull();
        getStyle()
                .set("padding", "20px")
                .set("background-color", "#fafafa");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMaxWidth("1000px");
        mainLayout.setWidth("100%");
        mainLayout.getStyle().set("margin", "0 auto");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidth("100%");
        headerLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H2 title = new H2("Metody płatności");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#333");

        Span subtitle = new Span("Zarządzaj swoimi kartami płatniczymi");
        subtitle.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        titleSection.add(title, subtitle);

        Button addCardButton = new Button("Dodaj kartę", VaadinIcon.CREDIT_CARD.create());
        addCardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCardButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");
        addCardButton.addClickListener(e -> openAddCardDialog());

        headerLayout.add(titleSection, addCardButton);

        cardsContainer = new VerticalLayout();
        cardsContainer.setSpacing(true);
        cardsContainer.setPadding(false);
        cardsContainer.setWidth("100%");
        cardsContainer.getStyle().set("margin-top", "30px");

        mainLayout.add(headerLayout, cardsContainer);
        add(mainLayout);
    }

    private void loadPaymentMethods() {
        cardsContainer.removeAll();

        List<PaymentMethod> methods = paymentMethodService.findByUser(currentUser);

        if (methods.isEmpty()) {
            cardsContainer.add(createEmptyState());
        } else {
            Div cardsGrid = new Div();
            cardsGrid.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "repeat(auto-fill, minmax(360px, 1fr))")
                    .set("gap", "20px")
                    .set("width", "100%");

            methods.forEach(method -> cardsGrid.add(createPaymentCard(method)));
            cardsContainer.add(cardsGrid);
        }
    }

    private Component createEmptyState() {
        Div emptyState = new Div();
        emptyState.getStyle()
                .set("text-align", "center")
                .set("padding", "60px 20px")
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        Icon icon = VaadinIcon.CREDIT_CARD.create();
        icon.setSize("64px");
        icon.getStyle().set("color", "#ccc");

        H3 message = new H3("Nie masz jeszcze dodanych kart");
        message.getStyle()
                .set("color", "#666")
                .set("margin-top", "20px");

        Span hint = new Span("Dodaj kartę aby móc dokonywać rezerwacji");
        hint.getStyle()
                .set("color", "#999")
                .set("font-size", "14px");

        emptyState.add(icon, message, hint);
        return emptyState;
    }

    private Component createPaymentCard(PaymentMethod method) {
        VerticalLayout cardContainer = new VerticalLayout();
        cardContainer.setSpacing(false);
        cardContainer.setPadding(false);

        Div card = new Div();
        card.getStyle()
                .set("width", "100%")
                .set("aspect-ratio", "1.586")
                .set("background", getCardGradient(method.getCardBrand()))
                .set("border-radius", "12px")
                .set("padding", "24px")
                .set("color", "white")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)")
                .set("position", "relative")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "space-between")
                .set("transition", "transform 0.2s, box-shadow 0.2s")
                .set("cursor", "pointer");

        card.getElement().addEventListener("mouseenter", e -> card.getStyle()
                .set("transform", "translateY(-4px)")
                .set("box-shadow", "0 8px 20px rgba(0,0,0,0.2)"));

        card.getElement().addEventListener("mouseleave", e -> card.getStyle()
                .set("transform", "translateY(0)")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)"));

        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        header.setAlignItems(HorizontalLayout.Alignment.START);
        header.getStyle().set("margin-bottom", "20px");

        Div brandLogo = new Div();
        brandLogo.setText(method.getCardBrand().getDisplayName().toUpperCase());
        brandLogo.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "bold")
                .set("padding", "6px 12px")
                .set("background-color", "rgba(255,255,255,0.25)")
                .set("border-radius", "6px")
                .set("backdrop-filter", "blur(10px)");

        Div badges = new Div();
        if (method.isDefault()) {
            Span defaultBadge = new Span("✓ Domyślna");
            defaultBadge.getStyle()
                    .set("font-size", "11px")
                    .set("padding", "4px 10px")
                    .set("background-color", "rgba(255,255,255,0.3)")
                    .set("border-radius", "12px")
                    .set("font-weight", "600");
            badges.add(defaultBadge);
        }

        header.add(brandLogo, badges);

        Div chip = new Div();
        chip.getStyle()
                .set("width", "45px")
                .set("height", "35px")
                .set("background", "linear-gradient(135deg, rgba(255,255,255,0.4), rgba(255,255,255,0.2))")
                .set("border-radius", "6px")
                .set("margin-bottom", "auto");

        Span cardNumber = new Span(method.getMaskedCardNumber());
        cardNumber.getStyle()
                .set("font-size", "18px")
                .set("letter-spacing", "2px")
                .set("font-family", "'Courier New', monospace")
                .set("font-weight", "500")
                .set("margin", "auto 0");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        footer.setAlignItems(HorizontalLayout.Alignment.END);

        VerticalLayout holderInfo = new VerticalLayout();
        holderInfo.setSpacing(false);
        holderInfo.setPadding(false);

        Span holderLabel = new Span("WŁAŚCICIEL");
        holderLabel.getStyle()
                .set("font-size", "8px")
                .set("opacity", "0.8")
                .set("letter-spacing", "0.5px");

        Span holderName = new Span(method.getCardHolderName());
        holderName.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("margin-top", "2px");

        holderInfo.add(holderLabel, holderName);

        VerticalLayout expiryInfo = new VerticalLayout();
        expiryInfo.setSpacing(false);
        expiryInfo.setPadding(false);
        expiryInfo.setAlignItems(VerticalLayout.Alignment.END);

        Span expiryLabel = new Span("WAŻNA DO");
        expiryLabel.getStyle()
                .set("font-size", "8px")
                .set("opacity", "0.8")
                .set("letter-spacing", "0.5px");

        Span expiry = new Span(method.getExpiryMonth() + "/" + method.getExpiryYear());
        expiry.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("margin-top", "2px");

        expiryInfo.add(expiryLabel, expiry);

        footer.add(holderInfo, expiryInfo);

        card.add(header, chip, cardNumber, footer);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidth("100%");
        actions.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        actions.getStyle()
                .set("margin-top", "12px")
                .set("gap", "8px");

        if (!method.isDefault()) {
            Button setDefaultButton = new Button("Ustaw jako domyślną");
            setDefaultButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            setDefaultButton.addClickListener(e -> setAsDefault(method));
            actions.add(setDefaultButton);
        }

        Button deleteButton = new Button("Usuń", VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete(method));
        actions.add(deleteButton);

        cardContainer.add(card, actions);
        return cardContainer;
    }

    private String getCardGradient(CardBrand brand) {
        return switch (brand) {
            case VISA -> "linear-gradient(135deg, #5B86E5 0%, #36D1DC 100%)";
            case MASTERCARD -> "linear-gradient(135deg, #FF512F 0%, #F09819 100%)";
            case AMERICAN_EXPRESS -> "linear-gradient(135deg, #11998e 0%, #38ef7d 100%)";
            case MAESTRO -> "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)";
            case DISCOVER -> "linear-gradient(135deg, #fa709a 0%, #fee140 100%)";
            default -> "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
        };
    }

    private void openAddCardDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Dodaj nową kartę");
        dialog.setWidth("550px");
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("400px", 2)
        );

        TextField cardNumberField = new TextField("Numer karty");
        cardNumberField.setPlaceholder("1234 5678 9012 3456");
        cardNumberField.setRequired(true);
        cardNumberField.setMaxLength(19);
        cardNumberField.setPrefixComponent(VaadinIcon.CREDIT_CARD.create());

        Span detectedBrand = new Span();
        detectedBrand.getStyle()
                .set("font-size", "12px")
                .set("color", "#667eea")
                .set("font-weight", "500");

        cardNumberField.addValueChangeListener(e -> {
            String value = e.getValue().replaceAll("\\s", "");
            if (!value.isEmpty()) {
                String formatted = value.replaceAll("(.{4})", "$1 ").trim();
                if (!formatted.equals(e.getValue())) {
                    cardNumberField.setValue(formatted);
                }

                CardBrand brand = CardBrand.detectFromNumber(value);
                detectedBrand.setText("✓ " + brand.getDisplayName());
            } else {
                detectedBrand.setText("");
            }
        });

        TextField cardHolderField = new TextField("Imię i nazwisko (jak na karcie)");
        cardHolderField.setPlaceholder("JAN KOWALSKI");
        cardHolderField.setRequired(true);
        cardHolderField.addValueChangeListener(e -> cardHolderField.setValue(e.getValue().toUpperCase()));

        ComboBox<String> monthField = new ComboBox<>("Miesiąc");
        monthField.setItems("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        monthField.setPlaceholder("MM");
        monthField.setRequired(true);

        ComboBox<String> yearField = new ComboBox<>("Rok");
        String[] years = new String[15];
        int currentYear = java.time.Year.now().getValue();
        for (int i = 0; i < 15; i++) {
            years[i] = String.valueOf(currentYear + i).substring(2);
        }
        yearField.setItems(years);
        yearField.setPlaceholder("RR");
        yearField.setRequired(true);

        PasswordField cvvField = new PasswordField("CVV/CVC");
        cvvField.setPlaceholder("123");
        cvvField.setMaxLength(4);
        cvvField.setRequired(true);
        cvvField.setHelperText("3-4 cyfry z tyłu karty");

        H3 billingTitle = new H3("Adres rozliczeniowy");
        billingTitle.getStyle()
                .set("margin", "15px 0 5px 0")
                .set("font-size", "15px")
                .set("grid-column", "span 2")
                .set("color", "#667eea");

        User currentUser = authenticatedUser.get().orElseThrow();
        Address userAddress = currentUser.getAddress();

        TextField addressField = new TextField("Ulica i numer");
        addressField.setPlaceholder("ul. Przykładowa 123");
        addressField.setRequired(true);
        if (userAddress != null && userAddress.getStreet() != null) {
            addressField.setValue(userAddress.getStreet());
        }

        TextField cityField = new TextField("Miasto");
        cityField.setPlaceholder("Warszawa");
        cityField.setRequired(true);
        if (userAddress != null && userAddress.getCity() != null) {
            cityField.setValue(userAddress.getCity());
        }

        TextField zipField = new TextField("Kod pocztowy");
        zipField.setPlaceholder("00-000");
        zipField.setRequired(true);
        zipField.setPattern("\\d{2}-\\d{3}");
        if (userAddress != null && userAddress.getZipCode() != null) {
            zipField.setValue(userAddress.getZipCode());
        }

        ComboBox<String> countryField = new ComboBox<>("Kraj");
        countryField.setItems("Polska", "Niemcy", "Wielka Brytania");
        countryField.setRequired(true);
        if (userAddress != null && userAddress.getCountry() != null) {
            countryField.setValue(userAddress.getCountry());
        } else {
            countryField.setValue("Polska");
        }

        form.add(
                cardNumberField,
                detectedBrand,
                cardHolderField,
                monthField, yearField,
                cvvField,
                billingTitle,
                addressField,
                cityField,
                zipField,
                countryField
        );

        form.setColspan(cardNumberField, 2);
        form.setColspan(detectedBrand, 2);
        form.setColspan(cardHolderField, 2);
        form.setColspan(billingTitle, 2);
        form.setColspan(addressField, 2);

        VerticalLayout content = new VerticalLayout(form);
        content.setPadding(false);

        Button saveButton = new Button("Dodaj kartę", e -> {
            if (validateCardForm(cardNumberField.getValue(), cardHolderField.getValue(),
                    monthField.getValue(), yearField.getValue(), cvvField.getValue(),
                    addressField.getValue(), cityField.getValue(), zipField.getValue())) {

                try {
                    String cleanCardNumber = cardNumberField.getValue().replaceAll("\\s", "");
                    paymentMethodService.addCreditCard(
                            currentUser,
                            cleanCardNumber,
                            cardHolderField.getValue(),
                            monthField.getValue(),
                            yearField.getValue(),
                            cvvField.getValue(),
                            addressField.getValue(),
                            cityField.getValue(),
                            zipField.getValue(),
                            countryField.getValue()
                    );

                    loadPaymentMethods();
                    dialog.close();
                    showNotification("Karta została dodana! 🎉", NotificationVariant.LUMO_SUCCESS);
                } catch (IllegalArgumentException ex) {
                    showNotification(ex.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        Button cancelButton = new Button("Anuluj", e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.add(content);
        dialog.open();
    }

    private boolean validateCardForm(String cardNumber, String holder, String month,
                                     String year, String cvv, String address,
                                     String city, String zip) {
        if (cardNumber == null || cardNumber.replaceAll("\\s", "").length() < 13) {
            showNotification("Nieprawidłowy numer karty", NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (holder == null || holder.trim().length() < 3) {
            showNotification("Podaj imię i nazwisko", NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (month == null || year == null) {
            showNotification("Wybierz datę ważności", NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (cvv == null || cvv.length() < 3) {
            showNotification("Podaj kod CVV", NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (address == null || address.trim().isEmpty()) {
            showNotification("Podaj adres", NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (city == null || city.trim().isEmpty()) {
            showNotification("Podaj miasto", NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (zip == null || !zip.matches("\\d{2}-\\d{3}")) {
            showNotification("Kod pocztowy: XX-XXX", NotificationVariant.LUMO_ERROR);
            return false;
        }
        return true;
    }

    private void setAsDefault(PaymentMethod method) {
        paymentMethodService.setDefaultPaymentMethod(method.getId(), currentUser);
        loadPaymentMethods();
        showNotification("Karta ustawiona jako domyślna ✓", NotificationVariant.LUMO_SUCCESS);
    }

    private void confirmDelete(PaymentMethod method) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Potwierdź usunięcie");

        Span message = new Span("Czy na pewno chcesz usunąć kartę " + method.getDisplayName() + "?");
        confirmDialog.add(message);

        Button deleteButton = new Button("Usuń", e -> {
            paymentMethodService.deletePaymentMethod(method.getId(), currentUser);
            loadPaymentMethods();
            confirmDialog.close();
            showNotification("Karta została usunięta", NotificationVariant.LUMO_SUCCESS);
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Anuluj", e -> confirmDialog.close());

        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }
}