package com.example.application.views.apartment;

import com.example.application.entity.Address;
import com.example.application.entity.Apartment;
import com.example.application.entity.ApartmentImage;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "add-apartment", layout = MainLayout.class)
@PageTitle("Dodaj nieruchomość")
@RolesAllowed("LANDLORD")
public class AddApartmentView extends ApartmentFormView implements BeforeEnterObserver {

    private final ApartmentService apartmentService;

    public AddApartmentView(ApartmentService apartmentService, AuthenticatedUser authenticatedUser) {
        super(authenticatedUser.get().orElseThrow());
        this.apartmentService = apartmentService;
        configureBaseView("Dodaj nową nieruchomość", null);
    }

    @Override
    protected HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.getStyle()
                .set("margin-top", "30px")
                .set("justify-content", "flex-end")
                .set("gap", "15px");

        Button cancelButton = new Button("Anuluj", e -> UI.getCurrent().navigate(""));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button saveButton = new Button("Zapisz", e -> saveApartment());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        buttonLayout.add(cancelButton, saveButton);
        return buttonLayout;
    }

    @Override
    protected String getImageSectionInfo() {
        return "Dodaj zdjęcia swojej nieruchomości (maksymalnie 10 zdjęć, każde do 5MB)";
    }

    private void saveApartment() {
        try {
            if (uploadedImages.isEmpty()) {
                showNotification("Dodaj co najmniej jedno zdjęcie nieruchomości", NotificationVariant.LUMO_ERROR);
                return;
            }

            Apartment apartment = new Apartment();
            Address address = new Address();

            apartmentBinder.writeBean(apartment);
            addressBinder.writeBean(address);

            for (ImagePreview preview : uploadedImages) {
                ApartmentImage apartmentImage = new ApartmentImage();
                apartmentImage.setImageData(preview.imageData);
                apartmentImage.setFileName(preview.fileName);
                apartmentImage.setContentType(preview.contentType);
                apartmentImage.setMainImage(preview.isMain);
                apartment.addImage(apartmentImage);
            }

            apartmentService.save(apartment, address, currentUser);

            showNotification("Nieruchomość została dodana pomyślnie!", NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("");

        } catch (ValidationException e) {
            showNotification("Sprawdź poprawność wypełnienia formularza", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Wystąpił błąd podczas zapisywania: " + e.getMessage(),
                    NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!currentUser.isLandlord()) {
            showNotification("Tylko wynajmujący mogą dodawać nieruchomości", NotificationVariant.LUMO_ERROR);
            event.forwardTo("");
        }
    }
}