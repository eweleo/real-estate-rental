package com.example.application.views.offers;

import com.example.application.entity.Apartment;
import com.example.application.entity.ApartmentImage;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentService;
import com.example.application.services.FavoriteService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static com.example.application.utils.Utils.guestLabel;
import static com.example.application.utils.Utils.roomsLabel;

@Route(value = "apartment", layout = MainLayout.class)
@PageTitle("Szczegóły oferty")
@AnonymousAllowed
public class ApartmentDetailView extends Div implements HasUrlParameter<String> {

    private final ApartmentService apartmentService;
    private final FavoriteService favoriteService;
    private final AuthenticatedUser authenticatedUser;

    private Apartment apartment;
    private User currentUser;
    private Image mainImage;
    private Div imageGallery;
    private Button favoriteButton;
    private Icon heartIcon;

    public ApartmentDetailView(ApartmentService apartmentService,
                               FavoriteService favoriteService,
                               AuthenticatedUser authenticatedUser) {
        this.apartmentService = apartmentService;
        this.favoriteService = favoriteService;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void setParameter(BeforeEvent event, String uuid) {
        apartment = apartmentService.findByUuid(uuid);

        Optional<User> userOptional = authenticatedUser.get();
        currentUser = userOptional.orElse(null);

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
        mainLayout.setMaxWidth("1200px");
        mainLayout.setWidth("100%");
        mainLayout.getStyle()
                .set("margin", "0 auto")
                .set("padding", "30px 20px");

        Button backButton = new Button("Powrót do ofert", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate(""));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidth("100%");
        headerLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);

        H1 title = new H1(apartment.getTitle());
        title.getStyle()
                .set("margin", "20px 0 10px 0")
                .set("color", "#333");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        titleLayout.getStyle().set("gap", "15px");
        titleLayout.add(title);

        if (currentUser != null) {
            createFavoriteButton();
            titleLayout.add(favoriteButton);
        }

        HorizontalLayout locationLayout = new HorizontalLayout();
        locationLayout.getStyle().set("gap", "8px").set("margin-bottom", "20px");

        Icon locationIcon = VaadinIcon.MAP_MARKER.create();
        locationIcon.setSize("20px");
        locationIcon.getStyle().set("color", "#667eea");

        String locationText = apartment.getAddress().getCity();
        if (apartment.getAddress().getStreet() != null) {
            locationText += ", " + apartment.getAddress().getStreet();
        }

        Span location = new Span(locationText);
        location.getStyle()
                .set("font-size", "16px")
                .set("color", "#666");

        locationLayout.add(locationIcon, location);

        Div gallerySection = createImageGallery();

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidth("100%");
        contentLayout.setSpacing(true);
        contentLayout.getStyle().set("gap", "30px").set("align-items", "flex-start");

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("100%");
        leftColumn.getStyle().set("flex", "2");

        Div detailsCard = createDetailsCard();
        Div descriptionCard = createDescriptionCard();

        leftColumn.add(detailsCard, descriptionCard);

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.getStyle().set("flex", "1");

        Div bookingCard = createBookingCard();
        rightColumn.add(bookingCard);

        contentLayout.add(leftColumn, rightColumn);

        mainLayout.add(backButton, titleLayout, locationLayout, gallerySection, contentLayout);
        add(mainLayout);
    }

    private void createFavoriteButton() {
        boolean isFavorite = favoriteService.isFavorite(currentUser, apartment);

        favoriteButton = new Button();

        heartIcon = isFavorite ? VaadinIcon.HEART.create() : VaadinIcon.HEART_O.create();
        heartIcon.setSize("24px");

        favoriteButton.setIcon(heartIcon);
        favoriteButton.setText(isFavorite ? "Usuń z ulubionych" : "Dodaj do ulubionych");
        favoriteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        updateFavoriteButtonStyle(isFavorite);

        favoriteButton.addClickListener(e -> toggleFavorite());
    }

    private void toggleFavorite() {
        favoriteService.toggleFavorite(currentUser, apartment);
        boolean isFavorite = favoriteService.isFavorite(currentUser, apartment);

        favoriteButton.getElement().executeJs(
                "this.style.transform = 'scale(1.1)'; " +
                        "setTimeout(() => { this.style.transform = 'scale(1)'; }, 150);"
        );

        Icon newIcon = isFavorite ? VaadinIcon.HEART.create() : VaadinIcon.HEART_O.create();
        newIcon.setSize("24px");
        heartIcon = newIcon;
        favoriteButton.setIcon(newIcon);
        favoriteButton.setText(isFavorite ? "Usuń z ulubionych" : "Dodaj do ulubionych");

        updateFavoriteButtonStyle(isFavorite);
    }

    private void updateFavoriteButtonStyle(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.getStyle().set("color", "#e91e63");
            heartIcon.getStyle().set("color", "#e91e63");
        } else {
            favoriteButton.getStyle().set("color", "#667eea");
            heartIcon.getStyle().set("color", "#667eea");
        }
    }


    private Div createImageGallery() {
        Div galleryContainer = new Div();
        galleryContainer.getStyle()
                .set("margin-bottom", "30px");

        List<ApartmentImage> images = apartment.getImages();

        if (images.isEmpty()) {
            return createNoImagePlaceholder();
        }

        Div mainImageContainer = new Div();
        mainImageContainer.getStyle()
                .set("width", "100%")
                .set("height", "500px")
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("margin-bottom", "15px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15)");

        ApartmentImage firstImage = apartment.getMainImage() != null ?
                apartment.getMainImage() : images.get(0);

        mainImage = createImage(firstImage);
        mainImage.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("object-fit", "cover")
                .set("cursor", "pointer");

        mainImageContainer.add(mainImage);

        imageGallery = new Div();
        imageGallery.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(120px, 1fr))")
                .set("gap", "10px")
                .set("width", "100%");

        images.forEach(image -> {
            Div thumbnail = createThumbnail(image);
            imageGallery.add(thumbnail);
        });

        galleryContainer.add(mainImageContainer, imageGallery);
        return galleryContainer;
    }

    private Image createImage(ApartmentImage apartmentImage) {
        StreamResource resource = new StreamResource("img-" + apartmentImage.getId(),
                () -> new ByteArrayInputStream(apartmentImage.getImageData()));
        return new Image(resource, apartment.getTitle());
    }

    private Div createThumbnail(ApartmentImage image) {
        Div thumbnailContainer = new Div();
        thumbnailContainer.getStyle()
                .set("width", "100%")
                .set("height", "100px")
                .set("border-radius", "8px")
                .set("overflow", "hidden")
                .set("cursor", "pointer")
                .set("border", "3px solid transparent")
                .set("transition", "all 0.3s");

        Image thumbnail = createImage(image);
        thumbnail.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("object-fit", "cover");

        thumbnailContainer.add(thumbnail);

        thumbnailContainer.addClickListener(e -> {
            StreamResource resource = new StreamResource("main-img-" + image.getId(),
                    () -> new ByteArrayInputStream(image.getImageData()));
            mainImage.setSrc(resource);

            imageGallery.getChildren().forEach(child -> child.getStyle().set("border", "3px solid transparent"));
            thumbnailContainer.getStyle().set("border", "3px solid #667eea");
        });

        thumbnailContainer.getElement().addEventListener("mouseenter", evt -> {
            if (!thumbnailContainer.getStyle().get("border").contains("#667eea")) {
                thumbnailContainer.getStyle().set("border", "3px solid #ddd");
            }
        });

        thumbnailContainer.getElement().addEventListener("mouseleave", evt -> {
            if (!thumbnailContainer.getStyle().get("border").contains("#667eea")) {
                thumbnailContainer.getStyle().set("border", "3px solid transparent");
            }
        });

        if (image.isMainImage() || (apartment.getMainImage() == null &&
                apartment.getImages().indexOf(image) == 0)) {
            thumbnailContainer.getStyle().set("border", "3px solid #667eea");
        }

        return thumbnailContainer;
    }

    private Div createNoImagePlaceholder() {
        Div placeholder = new Div();
        placeholder.getStyle()
                .set("width", "100%")
                .set("height", "400px")
                .set("background-color", "#e0e0e0")
                .set("border-radius", "12px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "30px");

        Icon icon = VaadinIcon.PICTURE.create();
        icon.setSize("64px");
        icon.getStyle().set("color", "#999");

        placeholder.add(icon);
        return placeholder;
    }

    private Div createDetailsCard() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        HorizontalLayout features = new HorizontalLayout();
        features.setWidth("100%");
        features.getStyle()
                .set("gap", "30px")
                .set("flex-wrap", "wrap");

        features.add(
                createDetailItem(VaadinIcon.BED, "Pokoje", apartment.getRoomsNumber() + roomsLabel(apartment.getRoomsNumber())),
                createDetailItem(VaadinIcon.USERS, "Goście", guestLabel(apartment.getMaxPerson())),
                createDetailItem(VaadinIcon.MONEY, "Cena", String.format("%.2f PLN/noc", apartment.getPrice()))
        );

        card.add(features);
        return card;
    }

    private Div createDetailItem(VaadinIcon iconType, String label, String value) {
        Div item = new Div();
        item.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("text-align", "center")
                .set("min-width", "150px");

        Icon icon = iconType.create();
        icon.setSize("32px");
        icon.getStyle()
                .set("color", "#667eea")
                .set("margin-bottom", "10px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "14px")
                .set("color", "#999")
                .set("margin-bottom", "5px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "16px")
                .set("color", "#333")
                .set("font-weight", "500");

        item.add(icon, labelSpan, valueSpan);
        return item;
    }

    private Div createDescriptionCard() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("margin-bottom", "20px");

        H3 title = new H3("Opis");
        title.getStyle()
                .set("margin-top", "0")
                .set("color", "#333");

        Paragraph description = new Paragraph(apartment.getDescription());
        description.getStyle()
                .set("color", "#666")
                .set("line-height", "1.8")
                .set("font-size", "16px")
                .set("margin", "0");

        card.add(title, description);
        return card;
    }

    private Div createBookingCard() {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "30px")
                .set("position", "sticky")
                .set("top", "20px");

        Div priceSection = new Div();
        priceSection.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "20px")
                .set("padding-bottom", "20px")
                .set("border-bottom", "2px solid #f0f0f0");

        Span priceLabel = new Span("Cena za noc");
        priceLabel.getStyle()
                .set("display", "block")
                .set("color", "#666")
                .set("font-size", "14px")
                .set("margin-bottom", "10px");

        H2 price = new H2(String.format("%.2f PLN", apartment.getPrice()));
        price.getStyle()
                .set("margin", "0")
                .set("color", "#667eea")
                .set("font-size", "32px")
                .set("font-weight", "bold");

        priceSection.add(priceLabel, price);

        Div contactSection = new Div();
        contactSection.getStyle()
                .set("margin-bottom", "20px")
                .set("padding", "15px")
                .set("background-color", "#f8f9fa")
                .set("border-radius", "8px");

        H3 contactTitle = new H3("Kontakt");
        contactTitle.getStyle()
                .set("margin", "0 0 15px 0")
                .set("font-size", "16px")
                .set("color", "#333");

        Span landlordName = new Span(apartment.getLandlord().getFirstName() + " " +
                apartment.getLandlord().getLastName());
        landlordName.getStyle()
                .set("display", "block")
                .set("color", "#333")
                .set("font-weight", "500")
                .set("margin-bottom", "8px");

        Span email = new Span(apartment.getLandlord().getEmail());
        email.getStyle()
                .set("display", "block")
                .set("color", "#666")
                .set("font-size", "14px");

        contactSection.add(contactTitle, landlordName, email);

        if (currentUser != null) {
            Button bookButton = new Button("Zarezerwuj teraz");
            bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
            bookButton.setWidth("100%");
            bookButton.getStyle()
                    .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                    .set("border", "none")
                    .set("margin-bottom", "10px");
            bookButton.addClickListener(e -> UI.getCurrent().navigate("reservation/" + apartment.getUuid()));

            Span bookingNote = new Span("Dokładny adres zostanie udostępniony po potwierdzeniu rezerwacji");
            bookingNote.getStyle()
                    .set("display", "block")
                    .set("color", "#999")
                    .set("font-size", "12px")
                    .set("text-align", "center")
                    .set("font-style", "italic")
                    .set("margin-top", "15px")
                    .set("line-height", "1.4");

            card.add(priceSection, contactSection, bookButton, bookingNote);
        } else {
            Div loginPrompt = new Div();
            loginPrompt.getStyle()
                    .set("text-align", "center")
                    .set("padding", "20px")
                    .set("background-color", "#f8f9fa")
                    .set("border-radius", "8px")
                    .set("margin-top", "20px");

            Span promptText = new Span("Zaloguj się, aby dokonać rezerwacji");
            promptText.getStyle()
                    .set("display", "block")
                    .set("color", "#666")
                    .set("margin-bottom", "15px");

            Button loginButton = new Button("Zaloguj się");
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            loginButton.setWidth("100%");
            loginButton.addClickListener(e -> UI.getCurrent().navigate("login"));

            loginPrompt.add(promptText, loginButton);
            card.add(priceSection, contactSection, loginPrompt);
        }

        return card;
    }
}