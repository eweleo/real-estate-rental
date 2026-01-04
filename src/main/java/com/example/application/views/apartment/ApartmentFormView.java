package com.example.application.views.apartment;

import com.example.application.entity.Address;
import com.example.application.entity.Apartment;
import com.example.application.entity.ApartmentImage;
import com.example.application.entity.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ApartmentFormView extends Div {

    protected final User currentUser;
    protected final Binder<Apartment> apartmentBinder;
    protected final Binder<Address> addressBinder;

    protected TextField title;
    protected TextArea description;
    protected IntegerField maxPerson;
    protected IntegerField roomsNumber;
    protected NumberField price;

    protected TextField street;
    protected TextField city;
    protected TextField zipCode;
    protected TextField country;

    protected MultiFileMemoryBuffer imageBuffer;
    protected final List<ImagePreview> uploadedImages = new ArrayList<>();
    protected HorizontalLayout imagesPreviewLayout;
    protected ImagePreview mainImage;

    public ApartmentFormView(User currentUser) {
        this.currentUser = currentUser;
        this.apartmentBinder = new Binder<>(Apartment.class);
        this.addressBinder = new Binder<>(Address.class);
    }

    protected void configureBaseView(String headerText, Button backButton) {
        addClassName("apartment-form-view");
        setSizeFull();
        getStyle()
                .set("padding", "20px")
                .set("background-color", "#fafafa");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMaxWidth("900px");
        mainLayout.setWidth("100%");
        mainLayout.getStyle()
                .set("margin", "0 auto")
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px");

        H2 header = new H2(headerText);
        header.getStyle()
                .set("margin", backButton != null ? "10px 0 0 0" : "0")
                .set("color", "#333");

        VerticalLayout formLayout = createFormLayout();
        HorizontalLayout buttonLayout = createButtonLayout();

        if (backButton != null) {
            mainLayout.add(backButton, header, formLayout, buttonLayout);
        } else {
            mainLayout.add(header, formLayout, buttonLayout);
        }

        add(mainLayout);
        configureBinders();
    }

    protected VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSpacing(true);
        formLayout.setPadding(false);

        formLayout.add(createImageUploadSection());
        formLayout.add(createBasicInfoSection());
        formLayout.add(createAddressSection());

        return formLayout;
    }

    protected Div createImageUploadSection() {
        Div section = createSection("Zdjęcia nieruchomości");

        Span infoText = new Span(getImageSectionInfo());
        infoText.getStyle()
                .set("color", "#666")
                .set("font-size", "14px")
                .set("display", "block")
                .set("margin-bottom", "15px");

        imageBuffer = new MultiFileMemoryBuffer();
        Upload imageUpload = new Upload(imageBuffer);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");
        imageUpload.setMaxFiles(10);
        imageUpload.setMaxFileSize(5 * 1024 * 1024);
        imageUpload.setDropLabel(new Span("Przeciągnij zdjęcia tutaj lub kliknij aby wybrać"));
        imageUpload.getStyle().set("width", "100%");

        imageUpload.addSucceededListener(event -> {
            try {
                byte[] imageData = imageBuffer.getInputStream(event.getFileName()).readAllBytes();

                if (uploadedImages.size() < 10) {
                    ImagePreview preview = new ImagePreview(
                            imageData,
                            event.getFileName(),
                            event.getMIMEType(),
                            uploadedImages.isEmpty(),
                            null
                    );

                    uploadedImages.add(preview);

                    if (preview.isMain) {
                        if (mainImage != null) {
                            mainImage.isMain = false;
                        }
                        mainImage = preview;
                    }

                    updateImagesPreview();
                } else {
                    showNotification("Możesz mieć maksymalnie 10 zdjęć", NotificationVariant.LUMO_WARNING);
                }

            } catch (IOException e) {
                showNotification("Błąd podczas wczytywania zdjęcia", NotificationVariant.LUMO_ERROR);
            }
        });

        imageUpload.addFileRejectedListener(event ->
                showNotification(event.getErrorMessage(), NotificationVariant.LUMO_ERROR));

        imagesPreviewLayout = new HorizontalLayout();
        imagesPreviewLayout.setWidth("100%");
        imagesPreviewLayout.getStyle()
                .set("flex-wrap", "wrap")
                .set("gap", "15px")
                .set("margin-top", "20px");

        section.add(infoText, imageUpload, imagesPreviewLayout);
        return section;
    }

    protected Div createBasicInfoSection() {
        Div basicInfoSection = createSection("Podstawowe informacje");

        title = new TextField("Tytuł ogłoszenia");
        title.setWidth("100%");
        title.setRequired(true);
        title.setPlaceholder("np. Przytulne mieszkanie w centrum miasta");

        description = new TextArea("Opis");
        description.setWidth("100%");
        description.setHeight("150px");
        description.setRequired(true);
        description.setPlaceholder("Opisz nieruchomość, jej udogodnienia, lokalizację...");
        description.setMaxLength(4000);
        description.setHelperText("Maksymalnie 4000 znaków");

        maxPerson = new IntegerField("Maksymalna liczba osób");
        maxPerson.setWidth("100%");
        maxPerson.setMin(1);
        maxPerson.setMax(20);
        maxPerson.setValue(1);

        roomsNumber = new IntegerField("Liczba pokoi");
        roomsNumber.setWidth("100%");
        roomsNumber.setMin(1);
        roomsNumber.setMax(20);
        roomsNumber.setValue(1);

        price = new NumberField("Cena (PLN / noc)");
        price.setWidth("100%");
        price.setMin(0.0);
        price.setStep(0.01);
        price.setPrefixComponent(new Div("PLN"));
        price.setRequired(true);
        price.setHelperText("Cena za jedną noc");

        HorizontalLayout detailsLayout = new HorizontalLayout(maxPerson, roomsNumber);
        detailsLayout.setWidth("100%");
        detailsLayout.getStyle().set("gap", "20px");

        basicInfoSection.add(title, description, detailsLayout, price);
        return basicInfoSection;
    }

    protected Div createAddressSection() {
        Div addressSection = createSection("Adres nieruchomości");

        street = new TextField("Ulica");
        street.setWidth("60%");
        street.setRequired(true);

        HorizontalLayout streetLayout = new HorizontalLayout(street);
        streetLayout.setWidth("100%");
        streetLayout.getStyle().set("gap", "15px");

        city = new TextField("Miasto");
        city.setWidth("50%");
        city.setRequired(true);

        zipCode = new TextField("Kod pocztowy");
        zipCode.setWidth("25%");
        zipCode.setPlaceholder("00-000");
        zipCode.setRequired(true);

        country = new TextField("Kraj");
        country.setWidth("25%");
        country.setValue("Polska");
        country.setRequired(true);

        HorizontalLayout cityLayout = new HorizontalLayout(city, zipCode, country);
        cityLayout.setWidth("100%");
        cityLayout.getStyle().set("gap", "15px");

        addressSection.add(streetLayout, cityLayout);
        return addressSection;
    }

    protected void updateImagesPreview() {
        imagesPreviewLayout.removeAll();
        for (ImagePreview preview : uploadedImages) {
            imagesPreviewLayout.add(preview.createPreviewComponent());
        }
    }

    protected class ImagePreview {
        byte[] imageData;
        String fileName;
        String contentType;
        boolean isMain;
        Long existingImageId;

        public ImagePreview(byte[] imageData, String fileName, String contentType, boolean isMain, Long existingImageId) {
            this.imageData = imageData;
            this.fileName = fileName;
            this.contentType = contentType;
            this.isMain = isMain;
            this.existingImageId = existingImageId;
        }

        public Div createPreviewComponent() {
            Div container = new Div();
            container.getStyle()
                    .set("position", "relative")
                    .set("width", "150px")
                    .set("height", "150px")
                    .set("border-radius", "8px")
                    .set("overflow", "hidden")
                    .set("border", isMain ? "3px solid #667eea" : "1px solid #ddd")
                    .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

            StreamResource resource = new StreamResource(fileName,
                    () -> new ByteArrayInputStream(imageData));
            Image image = new Image(resource, fileName);
            image.setWidth("100%");
            image.setHeight("100%");
            image.getStyle().set("object-fit", "cover");

            if (isMain) {
                Div mainBadge = new Div();
                mainBadge.setText("GŁÓWNE");
                mainBadge.getStyle()
                        .set("position", "absolute")
                        .set("top", "5px")
                        .set("left", "5px")
                        .set("background-color", "#667eea")
                        .set("color", "white")
                        .set("padding", "3px 8px")
                        .set("border-radius", "4px")
                        .set("font-size", "11px")
                        .set("font-weight", "bold");
                container.add(mainBadge);
            }

            HorizontalLayout actions = new HorizontalLayout();
            actions.getStyle()
                    .set("position", "absolute")
                    .set("bottom", "5px")
                    .set("right", "5px")
                    .set("gap", "5px");

            if (!isMain) {
                Button setMainButton = new Button(new Icon(VaadinIcon.STAR));
                setMainButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
                setMainButton.getStyle()
                        .set("background-color", "white")
                        .set("color", "#667eea");
                setMainButton.setTooltipText("Ustaw jako główne");
                setMainButton.addClickListener(e -> {
                    if (mainImage != null) {
                        mainImage.isMain = false;
                    }
                    this.isMain = true;
                    mainImage = this;
                    updateImagesPreview();
                });
                actions.add(setMainButton);
            }

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.getStyle()
                    .set("background-color", "white")
                    .set("color", "#ff5252");
            deleteButton.setTooltipText("Usuń zdjęcie");
            deleteButton.addClickListener(e -> {
                uploadedImages.remove(this);
                if (this.isMain && !uploadedImages.isEmpty()) {
                    uploadedImages.get(0).isMain = true;
                    mainImage = uploadedImages.get(0);
                } else if (uploadedImages.isEmpty()) {
                    mainImage = null;
                }
                updateImagesPreview();
            });
            actions.add(deleteButton);

            container.add(image, actions);
            return container;
        }
    }

    protected Div createSection(String title) {
        Div section = new Div();
        section.getStyle().set("margin-bottom", "30px");

        H3 sectionTitle = new H3(title);
        sectionTitle.getStyle()
                .set("font-size", "20px")
                .set("font-weight", "600")
                .set("color", "#667eea")
                .set("margin", "0 0 20px 0")
                .set("padding-bottom", "10px")
                .set("border-bottom", "2px solid #e0e0e0");

        section.add(sectionTitle);
        return section;
    }

    protected void configureBinders() {
        apartmentBinder.forField(title)
                .asRequired("Tytuł jest wymagany")
                .withValidator(t -> t.length() >= 5, "Tytuł musi mieć co najmniej 5 znaków")
                .withValidator(t -> t.length() <= 100, "Tytuł nie może być dłuższy niż 100 znaków")
                .bind(Apartment::getTitle, Apartment::setTitle);

        apartmentBinder.forField(description)
                .asRequired("Opis jest wymagany")
                .withValidator(d -> d.length() >= 20, "Opis musi mieć co najmniej 20 znaków")
                .withValidator(d -> d.length() <= 4000, "Opis nie może być dłuższy niż 4000 znaków")
                .bind(Apartment::getDescription, Apartment::setDescription);

        apartmentBinder.forField(maxPerson)
                .asRequired("Maksymalna liczba osób jest wymagana")
                .withValidator(p -> p >= 1, "Minimalna liczba osób to 1")
                .withValidator(p -> p <= 20, "Maksymalna liczba osób to 20")
                .bind(Apartment::getMaxPerson, Apartment::setMaxPerson);

        apartmentBinder.forField(roomsNumber)
                .asRequired("Liczba pokoi jest wymagana")
                .withValidator(r -> r >= 1, "Minimalna liczba pokoi to 1")
                .withValidator(r -> r <= 20, "Maksymalna liczba pokoi to 20")
                .bind(Apartment::getRoomsNumber, Apartment::setRoomsNumber);

        apartmentBinder.forField(price)
                .asRequired("Cena jest wymagana")
                .withValidator(p -> p > 0, "Cena musi być większa od 0")
                .bind(Apartment::getPrice, Apartment::setPrice);

        addressBinder.forField(street)
                .asRequired("Ulica jest wymagana")
                .bind(Address::getStreet, Address::setStreet);

        addressBinder.forField(city)
                .asRequired("Miasto jest wymagane")
                .bind(Address::getCity, Address::setCity);

        addressBinder.forField(zipCode)
                .asRequired("Kod pocztowy jest wymagany")
                .withValidator(zip -> zip.matches("\\d{2}-\\d{3}"), "Kod pocztowy musi być w formacie XX-XXX")
                .bind(Address::getZipCode, Address::setZipCode);

        addressBinder.forField(country)
                .asRequired("Kraj jest wymagany")
                .bind(Address::getCountry, Address::setCountry);
    }

    protected void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }

    protected abstract HorizontalLayout createButtonLayout();
    protected abstract String getImageSectionInfo();
}