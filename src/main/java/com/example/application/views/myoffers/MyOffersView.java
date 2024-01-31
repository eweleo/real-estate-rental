package com.example.application.views.myoffers;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;


@PageTitle("Moje oferty")
@Route(value = "my-offers", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MyOffersView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final Button addOfferButton = new Button("Dodaj nową ofertę");

    public MyOffersView() {

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add();
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        buttonLayout.addClassName(Gap.MEDIUM);
        buttonLayout.setWidth("100%");
        buttonLayout.setHeight("min-content");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn3.getStyle().set("flex-grow", "1");
//        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutRow.add(layoutColumn3);
    }

    private void configureView(){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(addOfferButton);
        addOfferButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);
        getContent().add(buttonLayout);
    }

    private void configureListener() {
        addOfferButton.addClickListener(event -> UI.getCurrent().navigate(AddOfferView.class));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        configureListener();
        configureView();
    }
}
