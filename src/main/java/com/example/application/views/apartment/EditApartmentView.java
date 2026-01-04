package com.example.application.views.apartment;

import com.example.application.entity.Address;
import com.example.application.entity.Apartment;
import com.example.application.entity.ApartmentImage;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentService;
import com.example.application.views.account.UserLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "edit-apartment", layout = UserLayout.class)
@PageTitle("Edytuj apartament")
@RolesAllowed("LANDLORD")
public class EditApartmentView extends ApartmentFormView implements HasUrlParameter<String> {

    private final ApartmentService apartmentService;
    private Apartment apartment;
    private Address address;

    public EditApartmentView(ApartmentService apartmentService, AuthenticatedUser authenticatedUser) {
        super(authenticatedUser.get().orElseThrow());
        this.apartmentService = apartmentService;
    }

    @Override
    public void setParameter(BeforeEvent event, String uuid) {
        apartment = apartmentService.findByUuid(uuid);

        if (apartment == null || !apartment.getLandlord().getId().equals(currentUser.getId())) {
            UI.getCurrent().navigate("");
            return;
        }

        address = apartment.getAddress();

        Button backButton = new Button("Powrót", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate("my-apartments"));

        configureBaseView("Edytuj apartament", backButton);
        loadExistingData();
    }

    @Override
    protected HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.getStyle()
                .set("margin-top", "30px")
                .set("justify-content", "flex-end")
                .set("gap", "15px");

        Button cancelButton = new Button("Anuluj", e -> UI.getCurrent().navigate("my-apartments"));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button saveButton = new Button("Zapisz zmiany", e -> updateApartment());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        buttonLayout.add(cancelButton, saveButton);
        return buttonLayout;
    }

    @Override
    protected String getImageSectionInfo() {
        return "Zarządzaj zdjęciami apartamentu (maksymalnie 10 zdjęć, każde do 5MB)";
    }

    private void loadExistingData() {
        apartmentBinder.readBean(apartment);
        addressBinder.readBean(address);

        for (ApartmentImage img : apartment.getImages()) {
            ImagePreview preview = new ImagePreview(
                    img.getImageData(),
                    img.getFileName(),
                    img.getContentType(),
                    img.isMainImage(),
                    img.getId()
            );
            uploadedImages.add(preview);
            if (preview.isMain) {
                mainImage = preview;
            }
        }
        updateImagesPreview();
    }

    private void updateApartment() {
        try {
            if (uploadedImages.isEmpty()) {
                showNotification("Apartament musi mieć co najmniej jedno zdjęcie", NotificationVariant.LUMO_ERROR);
                return;
            }

            apartmentBinder.writeBean(apartment);
            addressBinder.writeBean(address);

            apartment.getImages().clear();

            for (ImagePreview preview : uploadedImages) {
                ApartmentImage apartmentImage = new ApartmentImage();
                apartmentImage.setImageData(preview.imageData);
                apartmentImage.setFileName(preview.fileName);
                apartmentImage.setContentType(preview.contentType);
                apartmentImage.setMainImage(preview.isMain);
                apartment.addImage(apartmentImage);
            }

            apartmentService.update(apartment);

            showNotification("Apartament został zaktualizowany!", NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("my-apartments");

        } catch (ValidationException e) {
            showNotification("Sprawdź poprawność wypełnienia formularza", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Wystąpił błąd podczas zapisywania: " + e.getMessage(),
                    NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }
}