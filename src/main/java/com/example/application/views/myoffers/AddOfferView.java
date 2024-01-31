package com.example.application.views.myoffers;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Dodaj ofertę")
@Route(value = "add-offer", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AddOfferView extends Composite<VerticalLayout> {
}
