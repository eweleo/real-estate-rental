package com.example.application.views.account;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "change-password", layout = UserLayout.class)
@PageTitle("Zmiana hasła")
@PermitAll
public class ChangePasswordView extends Div {

    private final UserService userService;
    private final User currentUser;

    private final Binder<PasswordChangeDTO> binder;

    private PasswordField currentPassword;
    private PasswordField newPassword;
    private PasswordField confirmPassword;

    public ChangePasswordView(UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;
        this.currentUser = authenticatedUser.get().orElseThrow();
        this.binder = new Binder<>(PasswordChangeDTO.class);

        configureView();
    }

    private void configureView() {
        setWidth("100%");
        getStyle()
                .set("padding", "20px")
                .set("max-width", "600px")
                .set("margin", "0 auto");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px");

        H2 header = new H2("Zmiana hasła");
        header.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#333");

        VerticalLayout formLayout = createFormLayout();
        HorizontalLayout buttonLayout = createButtonLayout();

        mainLayout.add(header, createSecurityTips(), formLayout, buttonLayout);
        add(mainLayout);

        configureBinder();
    }

    private Div createSecurityTips() {
        Div tipsBox = new Div();
        tipsBox.getStyle()
                .set("background-color", "#e3f2fd")
                .set("border", "1px solid #2196f3")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("margin-bottom", "30px");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        titleLayout.getStyle().set("gap", "8px").set("margin-bottom", "15px");

        Icon infoIcon = VaadinIcon.INFO_CIRCLE.create();
        infoIcon.setSize("20px");
        infoIcon.getStyle().set("color", "#1976d2");

        H4 tipsTitle = new H4("Wskazówki dotyczące bezpiecznego hasła:");
        tipsTitle.getStyle()
                .set("margin", "0")
                .set("color", "#1976d2")
                .set("font-size", "16px");

        titleLayout.add(infoIcon, tipsTitle);

        UnorderedList tipsList = new UnorderedList();
        tipsList.getStyle()
                .set("margin", "0")
                .set("padding-left", "20px")
                .set("color", "#1565c0");

        tipsList.add(
                new ListItem("Używaj co najmniej 8 znaków"),
                new ListItem("Łącz wielkie i małe litery"),
                new ListItem("Dodaj cyfry i znaki specjalne")
        );

        tipsBox.add(titleLayout, tipsList);
        return tipsBox;
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSpacing(true);
        formLayout.setPadding(false);
        formLayout.setWidth("100%");

        currentPassword = new PasswordField("Obecne hasło");
        currentPassword.setWidth("100%");
        currentPassword.setRequired(true);
        currentPassword.setPrefixComponent(VaadinIcon.LOCK.create());
        currentPassword.setPlaceholder("Wprowadź obecne hasło");

        newPassword = new PasswordField("Nowe hasło");
        newPassword.setWidth("100%");
        newPassword.setRequired(true);
        newPassword.setPrefixComponent(VaadinIcon.KEY.create());
        newPassword.setPlaceholder("Wprowadź nowe hasło");
        newPassword.setHelperText("Minimum 8 znaków");

        // Password strength indicator
        Div strengthIndicator = new Div();
        strengthIndicator.setWidth("100%");
        strengthIndicator.getStyle()
                .set("height", "4px")
                .set("background-color", "#e0e0e0")
                .set("border-radius", "2px")
                .set("margin-top", "5px")
                .set("transition", "all 0.3s");

        newPassword.addValueChangeListener(e -> {
            String password = e.getValue();
            int strength = calculatePasswordStrength(password);
            updateStrengthIndicator(strengthIndicator, strength);
        });

        confirmPassword = new PasswordField("Potwierdź nowe hasło");
        confirmPassword.setWidth("100%");
        confirmPassword.setRequired(true);
        confirmPassword.setPrefixComponent(VaadinIcon.CHECK.create());
        confirmPassword.setPlaceholder("Wprowadź ponownie nowe hasło");

        formLayout.add(currentPassword, newPassword, strengthIndicator, confirmPassword);
        return formLayout;
    }

    private int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0;

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strength++;

        return strength;
    }

    private void updateStrengthIndicator(Div indicator, int strength) {
        String color;
        String width;

        switch (strength) {
            case 0:
            case 1:
                color = "#f44336";
                width = "20%";
                break;
            case 2:
                color = "#ff9800";
                width = "40%";
                break;
            case 3:
                color = "#ffc107";
                width = "60%";
                break;
            case 4:
                color = "#8bc34a";
                width = "80%";
                break;
            case 5:
                color = "#4caf50";
                width = "100%";
                break;
            default:
                color = "#e0e0e0";
                width = "0%";
        }

        indicator.getStyle()
                .set("background-color", color)
                .set("width", width);
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.getStyle()
                .set("margin-top", "30px")
                .set("justify-content", "flex-end")
                .set("gap", "15px");

        Button cancelButton = new Button("Anuluj", e -> clearForm());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button saveButton = new Button("Zmień hasło", e -> changePassword());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        buttonLayout.add(cancelButton, saveButton);
        return buttonLayout;
    }

    private void configureBinder() {
        binder.forField(currentPassword)
                .asRequired("Obecne hasło jest wymagane")
                .bind(PasswordChangeDTO::getCurrentPassword, PasswordChangeDTO::setCurrentPassword);

        binder.forField(newPassword)
                .asRequired("Nowe hasło jest wymagane")
                .withValidator(new StringLengthValidator(
                        "Hasło musi mieć co najmniej 8 znaków", 8, null))
                .withValidator(pwd -> !pwd.equals(currentPassword.getValue()),
                        "Nowe hasło musi być inne niż obecne")
                .bind(PasswordChangeDTO::getNewPassword, PasswordChangeDTO::setNewPassword);

        binder.forField(confirmPassword)
                .asRequired("Potwierdzenie hasła jest wymagane")
                .withValidator(pwd -> pwd.equals(newPassword.getValue()),
                        "Hasła muszą być identyczne")
                .bind(PasswordChangeDTO::getConfirmPassword, PasswordChangeDTO::setConfirmPassword);
    }

    private void changePassword() {
        try {
            PasswordChangeDTO dto = new PasswordChangeDTO();
            binder.writeBean(dto);

            // Sprawdź czy obecne hasło jest poprawne
            if (!userService.verifyPassword(currentUser, dto.getCurrentPassword())) {
                showNotification("Nieprawidłowe obecne hasło", NotificationVariant.LUMO_ERROR);
                currentPassword.setInvalid(true);
                currentPassword.setErrorMessage("Nieprawidłowe hasło");
                return;
            }

            // Zmień hasło
            userService.changePassword(currentUser, dto.getNewPassword());

            showNotification("Hasło zostało zmienione pomyślnie!", NotificationVariant.LUMO_SUCCESS);
            clearForm();

        } catch (ValidationException e) {
            showNotification("Sprawdź poprawność wypełnienia formularza", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Wystąpił błąd: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void clearForm() {
        currentPassword.clear();
        newPassword.clear();
        confirmPassword.clear();
        currentPassword.setInvalid(false);
        binder.readBean(new PasswordChangeDTO());
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }

    // DTO class
    public static class PasswordChangeDTO {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
