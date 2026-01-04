package com.example.application.views.offers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.time.LocalDate;
import java.util.function.Consumer;

public class FilterPanel extends Div {

    @Getter
    private final FilterCriteria criteria = new FilterCriteria();

    private TextField cityFilter;
    private IntegerField minPriceFilter;
    private IntegerField maxPriceFilter;
    private IntegerField roomsFilter;
    private IntegerField guestsFilter;
    private DatePicker checkInDate;
    private DatePicker checkOutDate;
    private ComboBox<String> sortBy;

    private final Consumer<FilterCriteria> onSearch;
    private final Runnable onClear;

    public FilterPanel(Consumer<FilterCriteria> onSearch, Runnable onClear) {
        this.onSearch = onSearch;
        this.onClear = onClear;

        configurePanel();
    }

    private void configurePanel() {
        getStyle()
                .set("width", "100%")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("margin-top", "-40px")
                .set("padding", "0 20px 40px 20px");

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSpacing(false);
        wrapper.setPadding(false);
        wrapper.setMaxWidth("1200px");
        wrapper.setWidth("100%");

        wrapper.add(createMainFilters(), createSecondaryFilters());
        add(wrapper);
    }

    private Component createMainFilters() {
        HorizontalLayout filterContainer = new HorizontalLayout();
        filterContainer.setWidth("100%");
        filterContainer.setAlignItems(FlexComponent.Alignment.END);
        filterContainer.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.15)")
                .set("padding", "20px")
                .set("gap", "15px")
                .set("flex-wrap", "wrap");

        cityFilter = new TextField("Gdzie?");
        cityFilter.setPlaceholder("Miasto, region...");
        cityFilter.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        cityFilter.setWidth("220px");

        filterContainer.add(
                cityFilter,
                createDateFilter(),
                createGuestsFilter(),
                createRoomsFilter(),
                createPriceFilter(),
                createSearchButton()
        );

        return filterContainer;
    }

    private Component createDateFilter() {
        return createLabeledField("Termin", () -> {
            HorizontalLayout datesLayout = new HorizontalLayout();
            datesLayout.setSpacing(false);
            datesLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            datesLayout.getStyle()
                    .set("gap", "8px")
                    .set("border", "1px solid #d0d0d0")
                    .set("border-radius", "4px")
                    .set("padding", "0 10px")
                    .set("background-color", "#fafafa");

            checkInDate = createDatePicker("Zameldowanie");
            checkOutDate = createDatePicker("Wymeldowanie");

            checkInDate.addValueChangeListener(e -> {
                if (e.getValue() != null) {
                    checkOutDate.setMin(e.getValue().plusDays(1));
                    if (checkOutDate.getValue() != null && checkOutDate.getValue().isBefore(e.getValue())) {
                        checkOutDate.setValue(e.getValue().plusDays(1));
                    }
                }
            });

            datesLayout.add(checkInDate, createSeparator(), checkOutDate);
            return datesLayout;
        });
    }

    private DatePicker createDatePicker(String placeholder) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPlaceholder(placeholder);
        datePicker.setWidth("140px");
        datePicker.setMin(LocalDate.now());
        datePicker.getStyle()
                .set("border", "none")
                .set("background", "transparent");
        return datePicker;
    }

    private Component createGuestsFilter() {
        return createLabeledField("Goście", () -> {
            guestsFilter = new IntegerField();
            guestsFilter.setPlaceholder("Ilu?");
            guestsFilter.setMin(1);
            guestsFilter.setWidth("100px");
            guestsFilter.setPrefixComponent(VaadinIcon.USERS.create());
            return guestsFilter;
        });
    }

    private Component createRoomsFilter() {
        return createLabeledField("Pokoje", () -> {
            roomsFilter = new IntegerField();
            roomsFilter.setPlaceholder("Ile?");
            roomsFilter.setMin(1);
            roomsFilter.setWidth("100px");
            roomsFilter.setPrefixComponent(VaadinIcon.BED.create());
            return roomsFilter;
        });
    }

    private Component createPriceFilter() {
        return createLabeledField("Budżet (PLN/noc)", () -> {
            HorizontalLayout priceLayout = new HorizontalLayout();
            priceLayout.setSpacing(false);
            priceLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            priceLayout.getStyle()
                    .set("gap", "8px")
                    .set("border", "1px solid #d0d0d0")
                    .set("border-radius", "4px")
                    .set("padding", "0 10px")
                    .set("background-color", "#fafafa");

            minPriceFilter = createPriceField("Min");
            maxPriceFilter = createPriceField("Max");

            priceLayout.add(minPriceFilter, createSeparator(), maxPriceFilter);
            return priceLayout;
        });
    }

    private IntegerField createPriceField(String placeholder) {
        IntegerField field = new IntegerField();
        field.setPlaceholder(placeholder);
        field.setMin(0);
        field.setWidth("80px");
        field.getStyle()
                .set("border", "none")
                .set("background", "transparent");
        return field;
    }

    private Component createLabeledField(String labelText, java.util.function.Supplier<Component> fieldSupplier) {
        Div container = new Div();
        container.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "5px");

        Span label = new Span(labelText);
        label.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("color", "#333");

        container.add(label, fieldSupplier.get());
        return container;
    }

    private Span createSeparator() {
        Span separator = new Span("—");
        separator.getStyle()
                .set("color", "#666")
                .set("font-weight", "bold");
        return separator;
    }

    private Component createSearchButton() {
        Button searchButton = new Button("Szukaj", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("margin-top", "auto");
        searchButton.addClickListener(e -> {
            updateCriteria();
            onSearch.accept(criteria);
        });
        return searchButton;
    }

    private Component createSecondaryFilters() {
        HorizontalLayout secondRow = new HorizontalLayout();
        secondRow.setWidth("100%");
        secondRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        secondRow.setAlignItems(FlexComponent.Alignment.CENTER);
        secondRow.getStyle()
                .set("padding", "0 20px")
                .set("margin-top", "15px");

        sortBy = new ComboBox<>("Sortuj");
        sortBy.setItems("Cena: rosnąco", "Cena: malejąco", "Najnowsze");
        sortBy.setValue("Najnowsze");
        sortBy.setWidth("180px");
        sortBy.addValueChangeListener(e -> {
            updateCriteria();
            onSearch.accept(criteria);
        });

        Button clearButton = new Button("Wyczyść filtry", VaadinIcon.REFRESH.create());
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearButton.addClickListener(e -> {
            clearFilters();
            onClear.run();
        });

        secondRow.add(sortBy, clearButton);
        return secondRow;
    }

    private void updateCriteria() {
        criteria.setCity(cityFilter.getValue());
        criteria.setMinPrice(minPriceFilter.getValue());
        criteria.setMaxPrice(maxPriceFilter.getValue());
        criteria.setRooms(roomsFilter.getValue());
        criteria.setGuests(guestsFilter.getValue());
        criteria.setCheckIn(checkInDate.getValue());
        criteria.setCheckOut(checkOutDate.getValue());
        criteria.setSortBy(sortBy.getValue());
    }

    public void clearFilters() {
        cityFilter.clear();
        minPriceFilter.clear();
        maxPriceFilter.clear();
        roomsFilter.clear();
        guestsFilter.clear();
        checkInDate.clear();
        checkOutDate.clear();
        sortBy.setValue("Najnowsze");
        criteria.clear();
    }
}