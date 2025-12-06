package com.example.application.views.account;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Route(value = "account", layout = UserLayout.class)
@PageTitle("Moje Konto")
@PermitAll
@Uses(Icon.class)
public class MyAccountView extends Div implements HasComponents, HasStyle, BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private final User user;
    private Avatar avatar;
    private byte[] newProfilePicture;
    private Binder<User> binder;
    private H3 greeting;

    private TextField firstName;
    private TextField lastName;
    private EmailField email;
    private TextField phoneNumber;
    private TextField street;
    private IntegerField streetNumber;
    private IntegerField flatNumber;
    private TextField city;
    private TextField zipCode;
    private Button saveButton;

    public MyAccountView(AuthenticatedUser authenticatedUser, UserService userService) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.user = authenticatedUser.get().orElseThrow();
        this.binder = new Binder<>(User.class);

        configureView();
        configureBinder();
    }

    private void configureView() {
        setWidth("100%");

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSpacing(false);
        mainContainer.setPadding(false);
        mainContainer.setWidth("100%");

        Div avatarContainer = createAvatarUploader();

        greeting = new H3("Witaj, " + user.getFirstName());
        greeting.getStyle()
                .set("margin", "0")
                .set("color", "#333");

        HorizontalLayout headerLayout = new HorizontalLayout(avatarContainer, greeting);
        headerLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        headerLayout.getStyle()
                .set("gap", "20px")
                .set("margin-bottom", "30px");

        VerticalLayout formContainer = createFormContainer();

        mainContainer.add(headerLayout, formContainer);
        add(mainContainer);
    }

    private VerticalLayout createFormContainer() {
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setSpacing(true);
        formContainer.setPadding(true);
        formContainer.setWidth("100%");
        formContainer.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        firstName = new TextField("Imię");
        firstName.setRequired(true);
        firstName.setWidth("100%");

        lastName = new TextField("Nazwisko");
        lastName.setRequired(true);
        lastName.setWidth("100%");

        email = new EmailField("E-mail");
        email.setRequired(true);
        email.setWidth("100%");

        phoneNumber = new TextField("Numer telefonu");
        phoneNumber.setRequired(true);
        phoneNumber.setWidth("100%");
        phoneNumber.setPlaceholder("123456789");

        street = new TextField("Ulica");
        street.setWidth("100%");

        streetNumber = new IntegerField("Numer domu");
        streetNumber.setWidth("100%");

        flatNumber = new IntegerField("Numer mieszkania");
        flatNumber.setWidth("100%");

        HorizontalLayout numbersLayout = new HorizontalLayout(streetNumber, flatNumber);
        numbersLayout.setWidth("100%");
        numbersLayout.getStyle().set("gap", "15px");

        city = new TextField("Miasto");
        city.setWidth("100%");

        zipCode = new TextField("Kod pocztowy");
        zipCode.setWidth("100%");
        zipCode.setPlaceholder("00-000");

        HorizontalLayout cityLayout = new HorizontalLayout(city, zipCode);
        cityLayout.setWidth("100%");
        cityLayout.getStyle().set("gap", "15px");

        saveButton = new Button("Zapisz", e -> saveUserData());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-top", "10px");

        formContainer.add(
                firstName,
                lastName,
                email,
                phoneNumber,
                street,
                numbersLayout,
                cityLayout,
                saveButton
        );

        return formContainer;
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
                .bind(User::getEmail, User::setEmail);

        binder.forField(phoneNumber)
                .asRequired("Numer telefonu jest wymagany")
                .withValidator(phone -> phone.matches("\\d{9}"), "Numer telefonu musi składać się z 9 cyfr")
                .bind(User::getTelephoneNumber, User::setTelephoneNumber);

        binder.forField(street)
                .bind(User::getStreet, User::setStreet);

        binder.forField(streetNumber)
                .withValidator(number -> number == null || number > 0, "Numer domu musi być większy od 0")
                .bind(User::getStreetNumber, User::setStreetNumber);

        binder.forField(flatNumber)
                .withValidator(number -> number == null || number > 0, "Numer mieszkania musi być większy od 0")
                .bind(User::getFlatNumber, User::setFlatNumber);

        binder.forField(city)
                .bind(User::getCity, User::setCity);

        binder.forField(zipCode)
                .withValidator(zip -> zip == null || zip.isEmpty() || zip.matches("\\d{2}-\\d{3}"),
                        "Kod pocztowy musi być w formacie XX-XXX")
                .bind(User::getZipCode, User::setZipCode);

        binder.readBean(user);
    }

    private Div createAvatarUploader() {
        Div avatarWrapper = new Div();
        avatarWrapper.getStyle()
                .set("position", "relative")
                .set("display", "inline-block")
                .set("cursor", "pointer");

        avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        updateAvatarImage();
        avatar.setWidth("80px");
        avatar.setHeight("80px");
        avatar.getElement().setAttribute("tabindex", "-1");
        avatar.getStyle()
                .set("border", "3px solid #1976d2")
                .set("transition", "all 0.3s");

        Icon cameraIcon = VaadinIcon.PENCIL.create();
        cameraIcon.setSize("24px");
        cameraIcon.getStyle().set("color", "white");

        Div iconOverlay = new Div(cameraIcon);
        iconOverlay.getStyle()
                .set("position", "absolute")
                .set("bottom", "0")
                .set("right", "0")
                .set("background-color", "#1976d2")
                .set("border-radius", "50%")
                .set("width", "32px")
                .set("height", "32px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("border", "2px solid white")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024);
        upload.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("opacity", "0")
                .set("cursor", "pointer");

        upload.setDropLabel(null);
        upload.setUploadButton(new Button());

        upload.addSucceededListener(event -> {
            try {
                newProfilePicture = buffer.getInputStream().readAllBytes();
                updateAvatarWithNewImage(newProfilePicture);

                showNotification("Zdjęcie zostało wczytane. Kliknij 'Zapisz' aby zatwierdzić zmiany.",
                        NotificationVariant.LUMO_SUCCESS);

            } catch (IOException e) {
                showNotification("Błąd podczas wczytywania zdjęcia", NotificationVariant.LUMO_ERROR);
            }
        });

        upload.addFileRejectedListener(event -> {
            showNotification(event.getErrorMessage(), NotificationVariant.LUMO_ERROR);
        });

        avatarWrapper.getElement().addEventListener("mouseenter", e -> {
            avatar.getStyle().set("opacity", "0.8");
        });

        avatarWrapper.getElement().addEventListener("mouseleave", e -> {
            avatar.getStyle().set("opacity", "1");
        });

        avatarWrapper.add(avatar, iconOverlay, upload);
        return avatarWrapper;
    }

    private void updateAvatarImage() {
        StreamResource resource = new StreamResource("profile-pic",
                () -> new ByteArrayInputStream(user.getProfilePicture()));
        avatar.setImageResource(resource);
    }

    private void updateAvatarWithNewImage(byte[] imageData) {
        StreamResource newResource = new StreamResource("profile-pic",
                () -> new ByteArrayInputStream(imageData));
        avatar.setImageResource(newResource);
    }

    private void saveUserData() {
        try {
            binder.writeBean(user);

            if (newProfilePicture != null) {
                user.setProfilePicture(newProfilePicture);
            }

            userService.update(user);

            avatar.setName(user.getFirstName() + " " + user.getLastName());
            greeting.setText("Witaj, " + user.getFirstName());

            UI.getCurrent().getPage().reload();

            showNotification("Dane zostały zapisane pomyślnie!", NotificationVariant.LUMO_SUCCESS);

            newProfilePicture = null;

        } catch (ValidationException e) {
            showNotification("Sprawdź poprawność wprowadzonych danych", NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }
}