package com.example.application.views.transaction;

import com.example.application.entity.Reservation;
import com.example.application.entity.ReservationStatus;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ReservationService;
import com.example.application.views.account.UserLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "transaction-history", layout = UserLayout.class)
@PageTitle("Historia Transakcji")
@PermitAll
public class TransactionHistoryView extends Div {

    private final ReservationService reservationService;
    private final User currentUser;
    private final boolean isLandlord;

    private VerticalLayout transactionsContainer;
    private ComboBox<String> statusFilter;
    private List<Reservation> allMyReservations;
    private List<Reservation> allLandlordReservations;
    private boolean showingMyReservations = true;

    public TransactionHistoryView(ReservationService reservationService,
                                  AuthenticatedUser authenticatedUser) {
        this.reservationService = reservationService;
        this.currentUser = authenticatedUser.get().orElseThrow();
        this.isLandlord = currentUser.isLandlord();

        configureView();
    }

    private void configureView() {
        setWidth("100%");
        getStyle()
                .set("padding", "20px")
                .set("max-width", "1200px")
                .set("margin", "0 auto");

        H2 pageTitle = new H2("Historia transakcji");
        pageTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#333");

        add(pageTitle);

        if (isLandlord) {
            add(createViewTabs());
        }

        add(createFilters());

        transactionsContainer = new VerticalLayout();
        transactionsContainer.setSpacing(false);
        transactionsContainer.setPadding(false);
        transactionsContainer.setWidth("100%");
        transactionsContainer.getStyle().set("gap", "15px");

        add(transactionsContainer);

        loadTransactions();
    }

    private Tabs createViewTabs() {
        Tab myReservationsTab = new Tab(VaadinIcon.CALENDAR_USER.create(), new Span(" Moje rezerwacje"));
        Tab landlordReservationsTab = new Tab(VaadinIcon.BUILDING.create(), new Span(" Rezerwacje moich obiektów"));

        Tabs viewTabs = new Tabs(myReservationsTab, landlordReservationsTab);
        viewTabs.setWidth("100%");
        viewTabs.getStyle().set("margin-bottom", "20px");

        viewTabs.addSelectedChangeListener(e -> {
            showingMyReservations = viewTabs.getSelectedTab() == myReservationsTab;
            statusFilter.setValue("Wszystkie");
            filterTransactions();
        });

        return viewTabs;
    }

    private HorizontalLayout createFilters() {
        HorizontalLayout filters = new HorizontalLayout();
        filters.setWidth("100%");
        filters.setAlignItems(HorizontalLayout.Alignment.END);
        filters.getStyle()
                .set("gap", "15px")
                .set("margin-bottom", "20px")
                .set("flex-wrap", "wrap");

        statusFilter = new ComboBox<>("Filtruj według statusu");
        statusFilter.setItems("Wszystkie", "Oczekująca", "Potwierdzona", "Anulowana", "Zakończona");
        statusFilter.setValue("Wszystkie");
        statusFilter.setWidth("250px");
        statusFilter.addValueChangeListener(e -> filterTransactions());

        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");

        Button refreshButton = new Button("Odśwież", VaadinIcon.REFRESH.create());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> loadTransactions());

        filters.add(statusFilter, spacer, refreshButton);
        return filters;
    }

    private void loadTransactions() {
        allMyReservations = reservationService.findByUserId(currentUser.getId());
        allMyReservations.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        if (isLandlord) {
            allLandlordReservations = reservationService.findByLandlordId(currentUser.getId());
            allLandlordReservations.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
        }

        filterTransactions();
    }

    private void filterTransactions() {
        List<Reservation> dataSource = showingMyReservations ? allMyReservations : allLandlordReservations;
        String selectedStatus = statusFilter.getValue();

        List<Reservation> filtered;
        if ("Wszystkie".equals(selectedStatus)) {
            filtered = dataSource;
        } else {
            ReservationStatus status = TransactionFilterHelper.mapStatusFromDisplay(selectedStatus);
            filtered = dataSource.stream()
                    .filter(r -> r.getStatus() == status)
                    .collect(Collectors.toList());
        }

        displayTransactions(filtered);
    }

    private void displayTransactions(List<Reservation> reservations) {
        transactionsContainer.removeAll();

        if (reservations.isEmpty()) {
            transactionsContainer.add(
                    TransactionEmptyState.create(showingMyReservations)
            );
            return;
        }

        for (Reservation reservation : reservations) {
            transactionsContainer.add(
                    TransactionCard.create(reservation, showingMyReservations)
            );
        }
    }
}