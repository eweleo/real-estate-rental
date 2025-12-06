package com.example.application.views.login;

import com.example.application.entity.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Rejestracja")
@Route(value = "register")
public class RegisterView extends Div {

    private final UserService userService;
    private Binder<User> binder;

    private TextField firstName;
    private TextField lastName;
    private EmailField email;
    private PasswordField password;
    private PasswordField confirmPassword;
    private Button registerButton;

    public RegisterView(UserService userService) {
        this.userService = userService;
        this.binder = new Binder<>(User.class);

        setSizeFull();
        getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("padding", "20px");

        add(createRegisterForm());
        configureBinder();
    }

    private VerticalLayout createRegisterForm() {
        VerticalLayout registerForm = new VerticalLayout();
        registerForm.setWidth("450px");
        registerForm.setPadding(true);
        registerForm.setSpacing(true);
        registerForm.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 10px 40px rgba(0,0,0,0.2)");

        H1 appTitle = new H1("Nieruchomości");
        appTitle.getStyle()
                .set("margin", "0")
                .set("text-align", "center")
                .set("color", "#667eea")
                .set("font-size", "32px");

        H2 registerTitle = new H2("Utwórz konto");
        registerTitle.getStyle()
                .set("margin", "10px 0 20px 0")
                .set("text-align", "center")
                .set("color", "#333")
                .set("font-weight", "500")
                .set("font-size", "20px");

        firstName = new TextField("Imię");
        firstName.setWidth("100%");
        firstName.setRequired(true);

        lastName = new TextField("Nazwisko");
        lastName.setWidth("100%");
        lastName.setRequired(true);

        email = new EmailField("Email");
        email.setWidth("100%");
        email.setRequired(true);

        password = new PasswordField("Hasło");
        password.setWidth("100%");
        password.setRequired(true);
        password.setHelperText("Min. 8 znaków, wielka litera, mała litera i cyfra");

        confirmPassword = new PasswordField("Potwierdź hasło");
        confirmPassword.setWidth("100%");
        confirmPassword.setRequired(true);
        confirmPassword.getStyle().set("margin-bottom", "20px");

        registerButton = new Button("Zarejestruj się", e -> handleRegistration());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.setWidth("100%");
        registerButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("margin-bottom", "10px");

        Div loginSection = new Div();
        loginSection.getStyle()
                .set("text-align", "center")
                .set("padding-top", "20px")
                .set("border-top", "1px solid #e0e0e0");

        Paragraph loginText = new Paragraph("Masz już konto?");
        loginText.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#666");

        RouterLink loginLink = new RouterLink("Zaloguj się", LoginView.class);
        loginLink.getStyle()
                .set("color", "#667eea")
                .set("text-decoration", "none")
                .set("font-weight", "600")
                .set("font-size", "16px");

        loginSection.add(loginText, loginLink);

        registerForm.add(
                appTitle,
                registerTitle,
                firstName,
                lastName,
                email,
                password,
                confirmPassword,
                registerButton,
                loginSection
        );

        return registerForm;
    }

    private void configureBinder() {
        binder.forField(firstName)
                .asRequired("Imię jest wymagane")
                .withValidator(name -> name.length() >= 2, "Imię musi mieć co najmniej 2 znaki")
                .withValidator(name -> name.length() <= 50, "Imię nie może być dłuższe niż 50 znaków")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lastName)
                .asRequired("Nazwisko jest wymagane")
                .withValidator(name -> name.length() >= 2, "Nazwisko musi mieć co najmniej 2 znaki")
                .withValidator(name -> name.length() <= 50, "Nazwisko nie może być dłuższe niż 50 znaków")
                .bind(User::getLastName, User::setLastName);

        binder.forField(email)
                .asRequired("Email jest wymagany")
                .withValidator(new EmailValidator("Nieprawidłowy adres email"))
                .withValidator(e -> !userService.emailExists(e), "Email jest już zajęty")
                .bind(User::getEmail, User::setEmail);

        binder.forField(password)
                .asRequired("Hasło jest wymagane")
                .withValidator(pass -> pass.length() >= 8, "Hasło musi mieć co najmniej 8 znaków")
                .withValidator(pass -> pass.matches(".*[A-Z].*"), "Hasło musi zawierać co najmniej jedną wielką literę")
                .withValidator(pass -> pass.matches(".*[a-z].*"), "Hasło musi zawierać co najmniej jedną małą literę")
                .withValidator(pass -> pass.matches(".*\\d.*"), "Hasło musi zawierać co najmniej jedną cyfrę")
                .bind(user -> "", (user, pass) -> {});
    }

    private void handleRegistration() {
        try {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                showNotification("Hasła nie są identyczne", NotificationVariant.LUMO_ERROR);
                return;
            }

            User newUser = new User();
            binder.writeBean(newUser);

            userService.registerUser(
                    newUser.getEmail(),
                    password.getValue(),
                    newUser.getFirstName(),
                    newUser.getLastName()
            );

            showNotification("Konto zostało utworzone! Możesz się teraz zalogować.",
                    NotificationVariant.LUMO_SUCCESS);

            UI.getCurrent().getPage().executeJs(
                    "setTimeout(function(){ window.location.href = 'login'; }, 2000);"
            );

        } catch (ValidationException e) {
            showNotification("Sprawdź poprawność wprowadzonych danych", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Wystąpił błąd podczas rejestracji: " + e.getMessage(),
                    NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(4000);
    }
}