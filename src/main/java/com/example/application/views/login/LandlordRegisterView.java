package com.example.application.views.login;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.example.application.views.offers.OffersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Optional;

@AnonymousAllowed
@PageTitle("Rejestracja Wynajmującego")
@Route(value = "register-landlord")
public class LandlordRegisterView extends Div {

    private final UserService userService;

    private EmailField email;
    private PasswordField password;
    private TextField firstName;
    private TextField lastName;
    private TextField phoneNumber;

    private TextField companyName;
    private TextField nip;

    private TextField street;
    private TextField city;
    private TextField zipCode;

    private Div userInfoMessage;
    private boolean isExistingUser = false;

    private final boolean isLoggedIn;
    private final User loggedInUser;

    public LandlordRegisterView(UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;

        Optional<User> userOpt = authenticatedUser.get();
        this.isLoggedIn = userOpt.isPresent();
        this.loggedInUser = userOpt.orElse(null);

        setSizeFull();
        getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("padding", "200px 20px")
                .set("overflow", "auto");

        add(createRegisterForm());
    }

    private VerticalLayout createRegisterForm() {
        VerticalLayout registerForm = new VerticalLayout();
        registerForm.setWidth("550px");
        registerForm.setPadding(false);
        registerForm.setSpacing(false);
        registerForm.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 10px 40px rgba(0,0,0,0.2)")
                .set("padding", "40px");

        H1 appTitle = new H1("Nieruchomości");
        appTitle.getStyle()
                .set("margin", "0 0 10px 0")
                .set("text-align", "center")
                .set("color", "#667eea")
                .set("font-size", "32px")
                .set("font-weight", "600");

        H2 registerTitle = new H2("Utwórz konto");
        registerTitle.getStyle()
                .set("margin", "0 0 30px 0")
                .set("text-align", "center")
                .set("color", "#333")
                .set("font-weight", "400")
                .set("font-size", "24px");

        registerForm.add(appTitle, registerTitle);

        if (!isLoggedIn) {
            registerForm.add(createUserSection());
        } else {
            Paragraph userInfo = new Paragraph("Zalogowany jako: " + loggedInUser.getEmail());
            userInfo.getStyle()
                    .set("background-color", "#e3f2fd")
                    .set("padding", "15px")
                    .set("border-radius", "6px")
                    .set("margin", "0 0 20px 0")
                    .set("color", "#1976d2")
                    .set("font-weight", "500");
            registerForm.add(userInfo);
        }

        registerForm.add(createCompanySection());
        registerForm.add(createAddressSection());

        Button registerButton = new Button(
                isLoggedIn ? "Zarejestruj firmę" : "Zarejestruj się",
                e -> handleRegistration()
        );
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.setWidth("100%");
        registerButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("margin-top", "20px");

        Div backSection = new Div();
        backSection.getStyle()
                .set("text-align", "center")
                .set("padding-top", "20px")
                .set("border-top", "1px solid #e0e0e0")
                .set("margin-top", "20px");

        RouterLink backLink = new RouterLink(
                isLoggedIn ? "Anuluj" : "Powrót do logowania",
                isLoggedIn ? OffersView.class : LoginView.class
        );
        backLink.getStyle()
                .set("color", "#667eea")
                .set("text-decoration", "none")
                .set("font-weight", "600")
                .set("font-size", "16px");

        backSection.add(backLink);

        registerForm.add(registerButton, backSection);

        return registerForm;
    }

    private Div createUserSection() {
        Div section = new Div();
        section.getStyle()
                .set("margin-bottom", "20px")
                .set("padding", "20px")
                .set("background-color", "#f8f9fa")
                .set("border-radius", "8px");

        H3 sectionTitle = new H3("Dane użytkownika");
        sectionTitle.getStyle()
                .set("margin", "0 0 15px 0")
                .set("font-size", "18px")
                .set("color", "#333");

        userInfoMessage = new Div();
        userInfoMessage.getStyle()
                .set("background-color", "#fff3cd")
                .set("border", "1px solid #ffc107")
                .set("padding", "12px")
                .set("border-radius", "6px")
                .set("margin-bottom", "15px")
                .set("color", "#856404")
                .set("display", "none");

        email = new EmailField("Email");
        email.setWidth("100%");
        email.setRequired(true);

        email.addValueChangeListener(e -> {
            String emailValue = e.getValue();
            if (emailValue != null && emailValue.contains("@")) {
                checkAndFillUserData(emailValue);
            } else {
                clearUserData();
            }
        });

        firstName = new TextField("Imię");
        firstName.setWidth("100%");
        firstName.setRequired(true);

        lastName = new TextField("Nazwisko");
        lastName.setWidth("100%");
        lastName.setRequired(true);

        phoneNumber = new TextField("Telefon");
        phoneNumber.setWidth("100%");
        phoneNumber.setRequired(true);
        phoneNumber.setPlaceholder("123456789");

        password = new PasswordField("Hasło");
        password.setWidth("100%");
        password.setRequired(true);
        password.setHelperText("Podaj hasło do konta (minimum 8 znaków)");

        section.add(sectionTitle, userInfoMessage, email, firstName, lastName, phoneNumber, password);
        return section;
    }

    private void checkAndFillUserData(String emailValue) {
        Optional<User> userOpt = userService.findByEmail(emailValue);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.isLandlord()) {
                userInfoMessage.setText("⚠️ To konto jest już wynajmującym");
                userInfoMessage.getStyle()
                        .set("display", "block")
                        .set("background-color", "#f8d7da")
                        .set("border-color", "#f5c6cb")
                        .set("color", "#721c24");
                isExistingUser = false;
                return;
            }

            isExistingUser = true;

            firstName.setValue(user.getFirstName() != null ? user.getFirstName() : "");
            lastName.setValue(user.getLastName() != null ? user.getLastName() : "");
            phoneNumber.setValue(user.getTelephoneNumber() != null ? user.getTelephoneNumber() : "");

            if (user.getAddress() != null) {
                if (user.getAddress().getStreet() != null) street.setValue(user.getAddress().getStreet());
                if (user.getAddress().getCity() != null) city.setValue(user.getAddress().getCity());
                if (user.getAddress().getZipCode() != null) zipCode.setValue(user.getAddress().getZipCode());
            }

            userInfoMessage.setText("✓ Konto znalezione. Podaj hasło aby kontynuować.");
            userInfoMessage.getStyle()
                    .set("display", "block")
                    .set("background-color", "#d1ecf1")
                    .set("border-color", "#bee5eb")
                    .set("color", "#0c5460");

        } else {
            clearUserData();
        }
    }

    private void clearUserData() {
        isExistingUser = false;
        userInfoMessage.getStyle().set("display", "none");
    }

    private Div createCompanySection() {
        Div section = new Div();
        section.getStyle()
                .set("margin-bottom", "20px")
                .set("padding", "20px")
                .set("background-color", "#f8f9fa")
                .set("border-radius", "8px");

        H3 sectionTitle = new H3("Dane firmy");
        sectionTitle.getStyle()
                .set("margin", "0 0 15px 0")
                .set("font-size", "18px")
                .set("color", "#333");

        companyName = new TextField("Nazwa firmy");
        companyName.setWidth("100%");
        companyName.setRequired(true);

        nip = new TextField("NIP");
        nip.setWidth("100%");
        nip.setRequired(true);
        nip.setPlaceholder("1234567890");
        nip.setHelperText("10 cyfr bez kresek");

        section.add(sectionTitle, companyName, nip);
        return section;
    }

    private Div createAddressSection() {
        Div section = new Div();
        section.getStyle()
                .set("margin-bottom", "20px")
                .set("padding", "20px")
                .set("background-color", "#f8f9fa")
                .set("border-radius", "8px");

        H3 sectionTitle = new H3("Adres");
        sectionTitle.getStyle()
                .set("margin", "0 0 15px 0")
                .set("font-size", "18px")
                .set("color", "#333");

        street = new TextField("Ulica i numer");
        street.setWidth("100%");
        street.setRequired(true);
        street.setPlaceholder("ul. Marszałkowska 45/47, m. 12");

        city = new TextField("Miasto");
        city.setWidth("100%");
        city.setRequired(true);

        zipCode = new TextField("Kod pocztowy");
        zipCode.setWidth("100%");
        zipCode.setRequired(true);
        zipCode.setPlaceholder("00-000");

        if (isLoggedIn && loggedInUser != null && loggedInUser.getAddress() != null) {
            if (loggedInUser.getAddress().getStreet() != null) street.setValue(loggedInUser.getAddress().getStreet());
            if (loggedInUser.getAddress().getCity() != null) city.setValue(loggedInUser.getAddress().getCity());
            if (loggedInUser.getAddress().getZipCode() != null)
                zipCode.setValue(loggedInUser.getAddress().getZipCode());
        }

        section.add(sectionTitle, street, city, zipCode);
        return section;
    }

    private void handleRegistration() {
        try {
            if (!isLoggedIn) {
                if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                        lastName.isEmpty() || phoneNumber.isEmpty()) {
                    showNotification("Wszystkie pola użytkownika są wymagane", NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (password.getValue().length() < 8) {
                    showNotification("Hasło musi mieć minimum 8 znaków", NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!phoneNumber.getValue().matches("\\d{9}")) {
                    showNotification("Numer telefonu musi składać się z 9 cyfr", NotificationVariant.LUMO_ERROR);
                    return;
                }
            }

            if (companyName.isEmpty() || nip.isEmpty() || street.isEmpty() ||
                    city.isEmpty() || zipCode.isEmpty()) {
                showNotification("Wszystkie pola firmy i adresu są wymagane", NotificationVariant.LUMO_ERROR);
                return;
            }

            if (!nip.getValue().matches("\\d{10}")) {
                showNotification("NIP musi składać się z 10 cyfr", NotificationVariant.LUMO_ERROR);
                return;
            }

            if (!zipCode.getValue().matches("\\d{2}-\\d{3}")) {
                showNotification("Kod pocztowy musi być w formacie XX-XXX", NotificationVariant.LUMO_ERROR);
                return;
            }

            if (!isLoggedIn) {
                if (isExistingUser) {
                    userService.registerLandlord(
                            email.getValue(),
                            password.getValue(),
                            companyName.getValue(),
                            nip.getValue(),
                            street.getValue(),
                            city.getValue(),
                            zipCode.getValue()
                    );

                    showNotification("Twoje konto zostało podniesione do wynajmującego! Możesz się teraz zalogować.",
                            NotificationVariant.LUMO_SUCCESS);

                } else {
                    userService.registerLandlord(
                            email.getValue(),
                            password.getValue(),
                            firstName.getValue(),
                            lastName.getValue(),
                            phoneNumber.getValue(),
                            companyName.getValue(),
                            nip.getValue(),
                            street.getValue(),
                            city.getValue(),
                            zipCode.getValue()
                    );

                    showNotification("Konto wynajmującego zostało utworzone! Możesz się teraz zalogować.",
                            NotificationVariant.LUMO_SUCCESS);
                }

                UI.getCurrent().navigate(LoginView.class);

            } else {
                userService.registerLandlord(
                        loggedInUser.getEmail(),
                        companyName.getValue(),
                        nip.getValue(),
                        street.getValue(),
                        city.getValue(),
                        zipCode.getValue()
                );

                showNotification("Firma została zarejestrowana pomyślnie! Możesz teraz wynajmować nieruchomości.",
                        NotificationVariant.LUMO_SUCCESS);

                UI.getCurrent().getPage().reload();
            }

        } catch (IllegalArgumentException e) {
            showNotification(e.getMessage(), NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Wystąpił błąd podczas rejestracji: " + e.getMessage(),
                    NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(4000);
    }
}