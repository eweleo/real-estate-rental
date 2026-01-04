package com.example.application.views.transaction;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class TransactionEmptyState {

    public static Div create(boolean showingMyReservations) {
        Div emptyState = new Div();
        emptyState.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("padding", "80px 40px")
                .set("text-align", "center");

        Icon icon = VaadinIcon.CALENDAR.create();
        icon.setSize("64px");
        icon.getStyle()
                .set("color", "#ccc")
                .set("margin-bottom", "20px");

        String emptyTitle = showingMyReservations ?
                "Brak rezerwacji" :
                "Brak rezerwacji Twoich apartamentów";

        String emptyDescription = showingMyReservations ?
                "Nie masz jeszcze żadnych rezerwacji" :
                "Twoje apartamenty nie mają jeszcze rezerwacji";

        H3 title = new H3(emptyTitle);
        title.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#666");

        Paragraph description = new Paragraph(emptyDescription);
        description.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#999");

        Button actionButton;
        if (showingMyReservations) {
            actionButton = new Button("Przeglądaj oferty", e -> UI.getCurrent().navigate(""));
        } else {
            actionButton = new Button("Dodaj apartament", e -> UI.getCurrent().navigate("add-apartment"));
        }

        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");

        emptyState.add(icon, title, description, actionButton);
        return emptyState;
    }
}