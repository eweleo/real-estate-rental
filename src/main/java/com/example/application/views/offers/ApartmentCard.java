package com.example.application.views.offers;

import com.example.application.entity.Apartment;
import com.example.application.entity.ApartmentImage;
import com.example.application.entity.User;

import com.example.application.services.FavoriteService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

import static com.example.application.utils.Utils.guestLabel;
import static com.example.application.utils.Utils.roomsLabel;

public class ApartmentCard extends Div {

    private final Apartment apartment;
    private final FavoriteService favoriteService;
    private final User currentUser;
    private Button favoriteButton;
    private Icon heartIcon;

    public ApartmentCard(Apartment apartment, FavoriteService favoriteService, User currentUser) {
        this.apartment = apartment;
        this.favoriteService = favoriteService;
        this.currentUser = currentUser;
        configureCard();
    }

    private void configureCard() {
        addClassName("apartment-card");
        setCardStyles();
        addHoverEffects();
        add(createImageSection(), createContentSection());
    }

    private void setCardStyles() {
        getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("overflow", "hidden")
                .set("transition", "transform 0.3s, box-shadow 0.3s")
                .set("height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("position", "relative");
    }

    private void addHoverEffects() {
        getElement().addEventListener("mouseenter", e ->
                getStyle()
                        .set("transform", "translateY(-5px)")
                        .set("box-shadow", "0 8px 16px rgba(0,0,0,0.15)")
        );

        getElement().addEventListener("mouseleave", e ->
                getStyle()
                        .set("transform", "translateY(0)")
                        .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
        );
    }

    private Component createImageSection() {
        Div container = new Div();
        container.getStyle()
                .set("width", "100%")
                .set("height", "250px")
                .set("overflow", "hidden")
                .set("position", "relative");

        // Warstwa klikalna pod wszystkim
        Div clickableArea = new Div();
        clickableArea.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("cursor", "pointer")
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("z-index", "0");

        clickableArea.addClickListener(e -> navigateToDetails());

        // Obraz i badge z wyłączoną interakcją
        Component image = createImage();
        Component priceBadge = createPriceBadge();

        container.add(clickableArea, image, priceBadge);

        // Przycisk ulubione (tylko dla zalogowanych) - najwyższy z-index
        if (currentUser != null) {
            container.add(createFavoriteButton());
        }

        return container;
    }

    private void navigateToDetails() {
        UI.getCurrent().navigate("apartment/" + apartment.getUuid());
    }

    private Component createImage() {
        ApartmentImage mainImage = apartment.getMainImage();

        if (mainImage != null) {
            Image img = createRealImage(mainImage);
            img.getStyle()
                    .set("pointer-events", "none"); // Zablokuj kliknięcia
            return img;
        } else {
            Div placeholder = createPlaceholder();
            placeholder.getStyle()
                    .set("pointer-events", "none");
            return placeholder;
        }
    }

    private Image createRealImage(ApartmentImage mainImage) {
        StreamResource imageResource = new StreamResource(
                "apartment-" + apartment.getId(),
                () -> new ByteArrayInputStream(mainImage.getImageData())
        );

        Image image = new Image(imageResource, apartment.getTitle());
        image.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("object-fit", "cover")
                .set("position", "relative")
                .set("z-index", "0");

        return image;
    }

    private Div createPlaceholder() {
        Div placeholder = new Div();
        placeholder.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("background-color", "#e0e0e0")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("position", "relative")
                .set("z-index", "0");

        Icon icon = VaadinIcon.PICTURE.create();
        icon.setSize("64px");
        icon.getStyle().set("color", "#999");
        placeholder.add(icon);

        return placeholder;
    }

    private Component createPriceBadge() {
        Div badge = new Div();
        badge.getStyle()
                .set("position", "absolute")
                .set("top", "15px")
                .set("right", "15px")
                .set("background-color", "rgba(102, 126, 234, 0.95)")
                .set("color", "white")
                .set("padding", "8px 15px")
                .set("border-radius", "20px")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.2)")
                .set("z-index", "1")
                .set("pointer-events", "none"); // Zablokuj kliknięcia

        badge.setText(String.format("%.2f PLN/noc", apartment.getPrice()));
        return badge;
    }

    private Component createFavoriteButton() {
        boolean isFavorite = favoriteService.isFavorite(currentUser, apartment);

        favoriteButton = new Button();
        favoriteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        heartIcon = isFavorite ? VaadinIcon.HEART.create() : VaadinIcon.HEART_O.create();
        heartIcon.setSize("24px");
        favoriteButton.setIcon(heartIcon);

        favoriteButton.getStyle()
                .set("position", "absolute")
                .set("top", "15px")
                .set("left", "15px")
                .set("background-color", "rgba(255, 255, 255, 0.95)")
                .set("border-radius", "50%")
                .set("width", "40px")
                .set("height", "40px")
                .set("min-width", "40px")
                .set("padding", "0")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.2)")
                .set("z-index", "3") // Najwyższy z-index
                .set("cursor", "pointer")
                .set("transition", "all 0.3s");

        updateHeartColor(isFavorite);

        // Hover effect
        favoriteButton.getElement().addEventListener("mouseenter", e -> {
            favoriteButton.getStyle()
                    .set("background-color", "rgba(255, 255, 255, 1)")
                    .set("transform", "scale(1.1)");
            if (!isFavorite) {
                heartIcon.getStyle().set("color", "#e91e63");
            }
        });

        favoriteButton.getElement().addEventListener("mouseleave", e -> {
            favoriteButton.getStyle()
                    .set("background-color", "rgba(255, 255, 255, 0.95)")
                    .set("transform", "scale(1)");
            updateHeartColor(favoriteService.isFavorite(currentUser, apartment));
        });

        // Tylko kliknięcie - toggle ulubione
        favoriteButton.addClickListener(e -> toggleFavorite());

        return favoriteButton;
    }

    private void toggleFavorite() {
        favoriteService.toggleFavorite(currentUser, apartment);
        boolean isFavorite = favoriteService.isFavorite(currentUser, apartment);

        // Animacja zmiany ikony
        favoriteButton.getElement().executeJs(
                "this.style.transform = 'scale(1.3)'; " +
                        "setTimeout(() => { this.style.transform = 'scale(1)'; }, 200);"
        );

        // Zmień ikonę
        Icon newIcon = isFavorite ? VaadinIcon.HEART.create() : VaadinIcon.HEART_O.create();
        newIcon.setSize("24px");
        heartIcon = newIcon;
        favoriteButton.setIcon(newIcon);

        updateHeartColor(isFavorite);
    }

    private void updateHeartColor(boolean isFavorite) {
        if (isFavorite) {
            heartIcon.getStyle().set("color", "#e91e63");
        } else {
            heartIcon.getStyle().set("color", "#666");
        }
    }

    private Component createContentSection() {
        VerticalLayout content = new VerticalLayout();
        content.getStyle()
                .set("padding", "20px")
                .set("flex", "1")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("cursor", "pointer");

        content.addClickListener(e -> navigateToDetails());

        content.add(
                createTitle(),
                createLocation(),
                createDescription(),
                createFeatures()
        );

        return content;
    }

    private Component createTitle() {
        H2 title = new H2(apartment.getTitle());
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("font-size", "20px")
                .set("color", "#333")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");
        return title;
    }

    private Component createLocation() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.getStyle()
                .set("gap", "5px")
                .set("margin-bottom", "15px");

        Icon icon = VaadinIcon.MAP_MARKER.create();
        icon.setSize("16px");
        icon.getStyle().set("color", "#667eea");

        Span location = new Span(apartment.getAddress().getCity());
        location.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        layout.add(icon, location);
        return layout;
    }

    private Component createDescription() {
        Span description = new Span(apartment.getDescription());
        description.getStyle()
                .set("color", "#666")
                .set("font-size", "14px")
                .set("line-height", "1.5")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("display", "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical")
                .set("margin-bottom", "15px")
                .set("flex", "1");
        return description;
    }

    private Component createFeatures() {
        HorizontalLayout features = new HorizontalLayout();
        features.setSpacing(true);
        features.getStyle()
                .set("gap", "15px")
                .set("margin-top", "auto")
                .set("padding-top", "15px")
                .set("border-top", "1px solid #e0e0e0");

        features.add(
                createFeatureItem(VaadinIcon.BED, apartment.getRoomsNumber() + roomsLabel(apartment.getRoomsNumber())),
                createFeatureItem(VaadinIcon.USERS, guestLabel(apartment.getMaxPerson()))
        );

        return features;
    }

    private Div createFeatureItem(VaadinIcon iconType, String text) {
        Div item = new Div();
        item.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "5px");

        Icon icon = iconType.create();
        icon.setSize("16px");
        icon.getStyle().set("color", "#667eea");

        Span label = new Span(text);
        label.getStyle()
                .set("color", "#666")
                .set("font-size", "13px");

        item.add(icon, label);
        return item;
    }
}