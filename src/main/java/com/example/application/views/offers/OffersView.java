package com.example.application.views.offers;


import com.example.application.entity.Apartment;
import com.example.application.services.ApartmentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.List;

@PageTitle("Oferty")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class OffersView extends Main implements HasComponents, HasStyle, BeforeEnterObserver {

    private final ApartmentService apartmentService;
    private OrderedList imageContainer = new OrderedList();
    private final DatePicker dateFrom = new DatePicker();
    private final DatePicker dataTo = new DatePicker();
    private final ComboBox<String> cityBox = new ComboBox<>();
    private final NumberField countPerson = new NumberField();
    private final Button searchButton = new Button("Szukaj");

    public OffersView(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }


    private void configureView() {
        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.Bottom.NONE, Margin.Top.XLARGE, Padding.NONE);

        add(getFilterLayout(), imageContainer);

    }

    private FormLayout getFilterLayout() {
        FormLayout filterLayout = new FormLayout();
        filterLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("900px", 6, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        filterLayout.addClassNames(Background.CONTRAST_5, BoxSizing.BORDER, Padding.LARGE, BorderRadius.LARGE,
                Position.STICKY, AlignItems.CENTER);
        UnorderedList ul = new UnorderedList();
        ul.addClassNames(ListStyleType.NONE, Margin.NONE, Padding.NONE, Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM);
        filterLayout.add(ul);

        dateFrom.setPlaceholder("Data zameldowania");
        dataTo.setPlaceholder("Data wymeldowania");
        configureComboBoxCity();

        countPerson.setStep(1);
        countPerson.setStepButtonsVisible(true);
        countPerson.setPlaceholder("Ilość osób");

        searchButton.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED, ButtonVariant.LUMO_PRIMARY);
        filterLayout.add(cityBox, dateFrom, new Text("-"), dataTo, countPerson, searchButton);

        return filterLayout;
    }

    private void configureComboBoxCity() {
        cityBox.setItems(List.of("Łódź", "Warszawa", "Kraków", "Gdańsk", "Wrocław", "Poznań"));
        cityBox.isClearButtonVisible();
        cityBox.setPlaceholder("Dokąd się wybierasz");
    }

    private void displayApartments(List<Apartment> apartments) {

        apartments.forEach(apartment -> imageContainer.add(new OfferViewCard(apartment)));
    }

    private void configListener() {
        searchButton.addClickListener(e -> {
            List<Apartment> apartments = apartmentService.findAll();
            if (cityBox.getOptionalValue().isPresent()) {
                apartments = apartments.stream().filter(apartment -> apartment.getAddress().getCity().equals(cityBox.getOptionalValue().get())).toList();
            }
            if (!countPerson.isEmpty()) {
                apartments = apartments.stream().filter(apartment -> apartment.getMaxPerson() == countPerson.getValue().intValue()).toList();
            }
            imageContainer.removeAll();
            displayApartments(apartments);
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        List<Apartment> apartments = apartmentService.findAll();
        configureView();
        displayApartments(apartments);
        configListener();
    }
}
