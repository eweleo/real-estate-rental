package com.example.application.views.account;


import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "favorites", layout = UserLayout.class)
@PageTitle("Ulubione")
@PermitAll

public class FavoritesView extends HorizontalLayout {

    FavoritesView(){
        add(new H1("Ulubione"));
    }
}
