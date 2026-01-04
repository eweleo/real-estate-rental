package com.example.application.views.offers;

import com.example.application.entity.Apartment;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentFilterService;
import com.example.application.services.ApartmentService;
import com.example.application.services.FavoriteService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Oferty Nieruchomości")
@AnonymousAllowed
public class OffersView extends Div {

    private final ApartmentService apartmentService;
    private final ApartmentFilterService filterService;
    private final FavoriteService favoriteService;
    private final User currentUser;
    private VerticalLayout offersContainer;
    private List<Apartment> allApartments;

    public OffersView(ApartmentService apartmentService, ApartmentFilterService filterService, FavoriteService favoriteService,
                      AuthenticatedUser authenticatedUser) {
        this.apartmentService = apartmentService;
        this.filterService = filterService;
        this.favoriteService = favoriteService;
        this.currentUser = authenticatedUser.get().orElse(null);

        configureView();
        loadApartments();
    }

    private void configureView() {
        setSizeFull();
        getStyle()
                .set("background-color", "#f5f5f5")
                .set("padding", "0");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        mainLayout.add(new HeroSection());

        FilterPanel filterPanel = new FilterPanel(this::handleSearch, this::handleClear);
        mainLayout.add(filterPanel);

        offersContainer = new VerticalLayout();
        offersContainer.setWidth("100%");
        offersContainer.setMaxWidth("1200px");
        offersContainer.getStyle().set("padding", "20px");

        mainLayout.add(offersContainer);
        add(mainLayout);
    }

    private void loadApartments() {
        allApartments = apartmentService.findAll();
        displayApartments(allApartments);
    }

    private void handleSearch(FilterCriteria criteria) {
        List<Apartment> filtered = filterService.filterAndSort(allApartments, criteria);
        displayApartments(filtered);
    }

    private void handleClear() {
        displayApartments(allApartments);
    }

    private void displayApartments(List<Apartment> apartments) {
        offersContainer.removeAll();

        if (apartments.isEmpty()) {
            offersContainer.add(createEmptyState());
            return;
        }

        offersContainer.add(createResultsHeader(apartments.size()));
        offersContainer.add(createApartmentsGrid(apartments));
    }

    private Component createResultsHeader(int count) {
        Div header = new Div();
        header.getStyle()
                .set("margin-bottom", "20px")
                .set("color", "#666")
                .set("font-size", "16px");

        String countText = count == 1 ? "ofertę" : count < 5 ? "oferty" : "ofert";
        header.setText("Znaleziono " + count + " " + countText);

        return header;
    }

    private Component createApartmentsGrid(List<Apartment> apartments) {
        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(350px, 1fr))")
                .set("gap", "25px")
                .set("width", "100%");

        apartments.forEach(apartment -> grid.add(new ApartmentCard(apartment,favoriteService,currentUser)));

        return grid;
    }

    private Component createEmptyState() {
        Div emptyState = new Div();
        emptyState.getStyle()
                .set("text-align", "center")
                .set("padding", "80px 40px")
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("max-width", "600px")
                .set("margin", "40px auto")
                .set("width", "100%");

        Icon icon = VaadinIcon.SEARCH.create();
        icon.setSize("64px");
        icon.getStyle().set("color", "#ccc");

        H2 message = new H2("Nie znaleziono ofert");
        message.getStyle()
                .set("color", "#666")
                .set("margin", "20px 0 10px 0")
                .set("font-size", "24px");

        Span hint = new Span("Spróbuj zmienić kryteria wyszukiwania");
        hint.getStyle()
                .set("color", "#999")
                .set("font-size", "16px")
                .set("display", "block");

        emptyState.add(icon, message, hint);
        return emptyState;
    }
}