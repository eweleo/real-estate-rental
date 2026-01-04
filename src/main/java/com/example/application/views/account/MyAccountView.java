package com.example.application.views.account;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Route(value = "my-account", layout = UserLayout.class)
@PageTitle("Moje Konto")
@PermitAll
@Uses(Icon.class)
public class MyAccountView extends Div implements HasComponents, HasStyle {

    private final UserService userService;
    private final User user;

    private Avatar avatar;
    private byte[] newProfilePicture;
    private H3 greeting;

    private AccountForm accountForm;

    public MyAccountView(AuthenticatedUser authenticatedUser, UserService userService) {
        this.userService = userService;
        this.user = authenticatedUser.get().orElseThrow();

        configureView();
    }

    private void configureView() {
        setWidth("100%");
        getStyle()
                .set("padding", "20px")
                .set("max-width", "800px")
                .set("margin", "0 auto");

        H2 pageTitle = new H2("Ustawienia konta");
        pageTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#333");

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSpacing(false);
        mainContainer.setPadding(false);
        mainContainer.setWidth("100%");

        HorizontalLayout headerLayout = createHeader();
        mainContainer.add(headerLayout);

        accountForm = new AccountForm(user, this::saveUserData);
        mainContainer.add(accountForm);

        add(pageTitle, mainContainer);
    }

    private HorizontalLayout createHeader() {
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

        return headerLayout;
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
                .set("border", "3px solid #667eea")
                .set("transition", "all 0.3s");

        Icon cameraIcon = VaadinIcon.CAMERA.create();
        cameraIcon.setSize("24px");
        cameraIcon.getStyle().set("color", "white");

        Div iconOverlay = new Div(cameraIcon);
        iconOverlay.getStyle()
                .set("position", "absolute")
                .set("bottom", "0")
                .set("right", "0")
                .set("background-color", "#667eea")
                .set("border-radius", "50%")
                .set("width", "32px")
                .set("height", "32px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("border", "2px solid white")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");

        Upload upload = createImageUpload();

        avatarWrapper.getElement().addEventListener("mouseenter", e -> avatar.getStyle().set("opacity", "0.8"));

        avatarWrapper.getElement().addEventListener("mouseleave", e -> avatar.getStyle().set("opacity", "1"));

        avatarWrapper.add(avatar, iconOverlay, upload);
        return avatarWrapper;
    }

    private Upload createImageUpload() {
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
        upload.setUploadButton(new com.vaadin.flow.component.button.Button());

        upload.addSucceededListener(event -> {
            try {
                newProfilePicture = buffer.getInputStream().readAllBytes();
                updateAvatarWithNewImage(newProfilePicture);
                showNotification("Zdjęcie zostało wczytane. Kliknij 'Zapisz zmiany' aby zatwierdzić.",
                        NotificationVariant.LUMO_SUCCESS);
            } catch (IOException e) {
                showNotification("Błąd podczas wczytywania zdjęcia", NotificationVariant.LUMO_ERROR);
            }
        });

        upload.addFileRejectedListener(event -> showNotification(event.getErrorMessage(), NotificationVariant.LUMO_ERROR));

        return upload;
    }

    private void updateAvatarImage() {
        if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
        }
    }

    private void updateAvatarWithNewImage(byte[] imageData) {
        StreamResource newResource = new StreamResource("profile-pic",
                () -> new ByteArrayInputStream(imageData));
        avatar.setImageResource(newResource);
    }

    private void saveUserData() {
        try {
            String newEmail = accountForm.getEmail();
            if (!newEmail.equals(user.getEmail())) {
                if (userService.emailExists(newEmail)) {
                    showNotification("Email jest już zajęty", NotificationVariant.LUMO_ERROR);
                    return;
                }
            }

            if (user.isLandlord() && user.getCompany() != null) {
                String newNip = accountForm.getNip();
                if (newNip != null && !newNip.equals(user.getCompany().getNip())) {
                    if (userService.nipExists(newNip)) {
                        showNotification("NIP jest już zajęty", NotificationVariant.LUMO_ERROR);
                        return;
                    }
                }

                accountForm.saveToCompany(user.getCompany());
            }

            accountForm.saveToUser(user);

            if (newProfilePicture != null) {
                user.setProfilePicture(newProfilePicture);
            }

            userService.update(user);

            avatar.setName(user.getFirstName() + " " + user.getLastName());
            greeting.setText("Witaj, " + user.getFirstName());

            showNotification("Dane zostały zaktualizowane! ✓", NotificationVariant.LUMO_SUCCESS);

            newProfilePicture = null;

        } catch (Exception e) {
            showNotification("Wystąpił błąd: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }
}