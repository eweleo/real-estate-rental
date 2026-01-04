package com.example.application.views.apartment;

import com.example.application.entity.Apartment;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentService;
import com.example.application.views.account.UserLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.util.List;

@Route(value = "my-apartments", layout = UserLayout.class)
@PageTitle("Moje apartamenty")
@RolesAllowed("LANDLORD")
public class MyApartmentsView extends Div {

    private final ApartmentService apartmentService;
    private final User currentUser;
    private VerticalLayout apartmentsContainer;

    public MyApartmentsView(ApartmentService apartmentService,
                            AuthenticatedUser authenticatedUser) {
        this.apartmentService = apartmentService;
        this.currentUser = authenticatedUser.get().orElseThrow();

        configureView();
    }

    private void configureView() {
        setWidth("100%");
        getStyle()
                .set("padding", "20px")
                .set("max-width", "1400px")
                .set("margin", "0 auto");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.getStyle().set("margin-bottom", "20px");

        H2 pageTitle = new H2("Moje apartamenty");
        pageTitle.getStyle()
                .set("margin", "0")
                .set("color", "#333");

        Button addButton = new Button("Dodaj apartament", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");
        addButton.addClickListener(e -> UI.getCurrent().navigate("add-apartment"));

        header.add(pageTitle, addButton);

        apartmentsContainer = new VerticalLayout();
        apartmentsContainer.setSpacing(false);
        apartmentsContainer.setPadding(false);
        apartmentsContainer.setWidth("100%");
        apartmentsContainer.getStyle().set("gap", "20px");

        add(header, apartmentsContainer);

        loadApartments();
    }

    private void loadApartments() {
        apartmentsContainer.removeAll();

        List<Apartment> apartments = apartmentService.findByLandlord(currentUser);

        if (apartments.isEmpty()) {
            apartmentsContainer.add(createEmptyState());
            return;
        }

        for (Apartment apartment : apartments) {
            apartmentsContainer.add(createApartmentCard(apartment));
        }
    }

    private Div createEmptyState() {
        Div emptyState = new Div();
        emptyState.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "80px 40px")
                .set("text-align", "center");

        Icon icon = VaadinIcon.BUILDING.create();
        icon.setSize("64px");
        icon.getStyle()
                .set("color", "#ccc")
                .set("margin-bottom", "20px");

        H3 title = new H3("Brak apartamentów");
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#666");

        Paragraph description = new Paragraph("Nie masz jeszcze żadnych apartamentów do wynajęcia");
        description.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#999");

        Button addButton = new Button("Dodaj pierwszy apartament", e ->
                UI.getCurrent().navigate("add-apartment"));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        emptyState.add(icon, title, description, addButton);
        return emptyState;
    }

    private Div createApartmentCard(Apartment apartment) {
        Div card = new Div();
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "20px")
                .set("transition", "all 0.3s");

        card.getElement().addEventListener("mouseenter", e -> card.getStyle().set("box-shadow", "0 4px 16px rgba(0,0,0,0.15)"));

        card.getElement().addEventListener("mouseleave", e -> card.getStyle().set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)"));

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        mainLayout.getStyle().set("gap", "20px");

        Div imageContainer = createImageSection(apartment);

        VerticalLayout infoLayout = createInfoSection(apartment);

        VerticalLayout actionsLayout = createActionsSection(apartment);

        mainLayout.add(imageContainer, infoLayout, actionsLayout);
        card.add(mainLayout);

        return card;
    }

    private Div createImageSection(Apartment apartment) {
        Div imageContainer = new Div();
        imageContainer.getStyle()
                .set("cursor", "pointer");

        if (apartment.getMainImage() != null) {
            Image thumbnail = new Image();
            StreamResource resource = new StreamResource("thumb",
                    () -> new ByteArrayInputStream(apartment.getMainImage().getImageData()));
            thumbnail.setSrc(resource);
            thumbnail.setWidth("200px");
            thumbnail.setHeight("150px");
            thumbnail.getStyle()
                    .set("border-radius", "8px")
                    .set("object-fit", "cover");
            imageContainer.add(thumbnail);
        } else {
            imageContainer.getStyle()
                    .set("width", "200px")
                    .set("height", "150px")
                    .set("background-color", "#e0e0e0")
                    .set("border-radius", "8px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center");
            Icon placeholderIcon = VaadinIcon.PICTURE.create();
            placeholderIcon.setSize("48px");
            placeholderIcon.getStyle().set("color", "#999");
            imageContainer.add(placeholderIcon);
        }

        imageContainer.addClickListener(e ->
                UI.getCurrent().navigate("apartment/" + apartment.getUuid()));

        return imageContainer;
    }

    private VerticalLayout createInfoSection(Apartment apartment) {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);
        infoLayout.getStyle()
                .set("flex", "1")
                .set("cursor", "pointer");

        infoLayout.addClickListener(e ->
                UI.getCurrent().navigate("apartment/" + apartment.getUuid()));

        H3 title = new H3(apartment.getTitle());
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#333")
                .set("font-size", "20px");

        HorizontalLayout locationLayout = new HorizontalLayout();
        locationLayout.setSpacing(false);
        locationLayout.getStyle().set("gap", "5px").set("margin-bottom", "10px");

        Icon locationIcon = VaadinIcon.MAP_MARKER.create();
        locationIcon.setSize("16px");
        locationIcon.getStyle().set("color", "#667eea");

        Span location = new Span(apartment.getAddress().getCity() + ", " +
                apartment.getAddress().getCountry());
        location.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        locationLayout.add(locationIcon, location);

        HorizontalLayout featuresLayout = new HorizontalLayout();
        featuresLayout.getStyle().set("gap", "15px").set("margin-bottom", "10px");

        Span rooms = new Span(VaadinIcon.BED.create(),
                new Span(" " + apartment.getRoomsNumber() + " pokoje"));
        rooms.getStyle().set("color", "#666").set("font-size", "14px");

        Span guests = new Span(VaadinIcon.USERS.create(),
                new Span(" do " + apartment.getMaxPerson() + " osób"));
        guests.getStyle().set("color", "#666").set("font-size", "14px");

        featuresLayout.add(rooms, guests);

        HorizontalLayout priceLayout = new HorizontalLayout();
        priceLayout.setAlignItems(HorizontalLayout.Alignment.BASELINE);
        priceLayout.getStyle().set("gap", "5px");

        H4 price = new H4(String.format("%.2f PLN", apartment.getPrice()));
        price.getStyle()
                .set("margin", "0")
                .set("color", "#667eea")
                .set("font-size", "24px");

        Span priceLabel = new Span("za noc");
        priceLabel.getStyle()
                .set("color", "#999")
                .set("font-size", "14px");

        priceLayout.add(price, priceLabel);

        infoLayout.add(title, locationLayout, featuresLayout, priceLayout);
        return infoLayout;
    }

    private VerticalLayout createActionsSection(Apartment apartment) {
        VerticalLayout actionsLayout = new VerticalLayout();
        actionsLayout.setSpacing(false);
        actionsLayout.setPadding(false);
        actionsLayout.setAlignItems(VerticalLayout.Alignment.END);
        actionsLayout.getStyle().set("gap", "10px");

        Button viewButton = new Button("Zobacz", VaadinIcon.EYE.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewButton.setWidth("150px");
        viewButton.addClickListener(e ->
                UI.getCurrent().navigate("apartment/" + apartment.getUuid()));

        Button editButton = new Button("Edytuj", VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.setWidth("150px");
        editButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");
        editButton.addClickListener(e ->
                UI.getCurrent().navigate("edit-apartment/" + apartment.getUuid()));

        Button reservationsButton = new Button("Rezerwacje", VaadinIcon.CALENDAR.create());
        reservationsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        reservationsButton.setWidth("150px");
        reservationsButton.addClickListener(e -> {
            UI.getCurrent().navigate("transaction-history");
        });

        Button deleteButton = new Button("Usuń", VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.setWidth("150px");
        deleteButton.addClickListener(e -> openDeleteDialog(apartment));

        actionsLayout.add(viewButton, editButton, reservationsButton, deleteButton);
        return actionsLayout;
    }

    private void openDeleteDialog(Apartment apartment) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Usuń apartament");
        dialog.setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        Div warningBox = new Div();
        warningBox.getStyle()
                .set("background-color", "#fff3cd")
                .set("border", "1px solid #ffc107")
                .set("border-radius", "8px")
                .set("padding", "15px")
                .set("margin-bottom", "15px");

        Icon warningIcon = VaadinIcon.WARNING.create();
        warningIcon.setSize("24px");
        warningIcon.getStyle()
                .set("color", "#856404")
                .set("margin-bottom", "10px");

        Paragraph warning = new Paragraph("Czy na pewno chcesz usunąć ten apartament?");
        warning.getStyle()
                .set("color", "#856404")
                .set("margin", "0 0 10px 0")
                .set("font-weight", "600");

        Paragraph warningDetails = new Paragraph(
                "Ta operacja jest nieodwracalna. Wszystkie dane apartamentu zostaną trwale usunięte.");
        warningDetails.getStyle()
                .set("color", "#856404")
                .set("margin", "0")
                .set("font-size", "14px");

        warningBox.add(warningIcon, warning, warningDetails);

        Div apartmentInfo = new Div();
        apartmentInfo.getStyle()
                .set("background-color", "#f8f9fa")
                .set("border-radius", "8px")
                .set("padding", "15px");

        H4 apartmentTitle = new H4(apartment.getTitle());
        apartmentTitle.getStyle()
                .set("margin", "0 0 5px 0")
                .set("color", "#333");

        Span apartmentLocation = new Span(apartment.getAddress().getCity());
        apartmentLocation.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        apartmentInfo.add(apartmentTitle, apartmentLocation);

        layout.add(warningBox, apartmentInfo);

        Button deleteButton = new Button("Usuń apartament", e -> {
            apartmentService.delete(apartment);
            dialog.close();
            showNotification();
            loadApartments();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Anuluj", e -> dialog.close());

        dialog.getFooter().add(cancelButton, deleteButton);
        dialog.add(layout);
        dialog.open();
    }

    private void showNotification() {
        Notification notification = Notification.show("Apartament został usunięty");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }
}
