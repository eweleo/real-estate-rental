package com.example.application.views.account;


import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

@ParentLayout(MainLayout.class)
public class UserLayout extends Div implements RouterLayout, BeforeEnterObserver {

    private RouterLink accountLink;
    private RouterLink favoritesLink;
    private Div contentArea;

    public UserLayout() {
        setSizeFull();
        getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("background-color", "#fafafa")
                .set("padding", "40px 20px");

        createLayout();
    }

    private void createLayout() {
        HorizontalLayout mainContainer = new HorizontalLayout();
        mainContainer.setSpacing(true);
        mainContainer.getStyle()
                .set("max-width", "1100px")
                .set("width", "100%")
                .set("gap", "30px")
                .set("align-items", "flex-start");

        VerticalLayout menu = new VerticalLayout();
        menu.setSpacing(false);
        menu.setPadding(true);
        menu.setWidth("250px");
        menu.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("flex-shrink", "0");

        Span menuTitle = new Span("Panel Użytkownika");
        menuTitle.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("padding", "0 0 20px 0")
                .set("display", "block")
                .set("color", "#333")
                .set("border-bottom", "2px solid #f0f0f0")
                .set("margin-bottom", "10px");

        accountLink = createMenuLink("Moje Konto", MyAccountView.class);
        favoritesLink = createMenuLink("Ulubione", FavoritesView.class);

        menu.add(menuTitle, accountLink, favoritesLink);

        contentArea = new Div();
        contentArea.getStyle()
                .set("flex", "1")
                .set("min-width", "0");

        mainContainer.add(menu, contentArea);
        add(mainContainer);
    }

    private RouterLink createMenuLink(String text, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        RouterLink link = new RouterLink(text, navigationTarget);
        link.getStyle()
                .set("padding", "14px 16px")
                .set("display", "block")
                .set("text-decoration", "none")
                .set("color", "#9e0b0b")
                .set("border-radius", "6px")
                .set("margin", "8px 0")
                .set("transition", "all 0.2s")
                .set("font-weight", "500");

        return link;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        resetMenuStyles();

        Class<?> activeView = event.getNavigationTarget();
        if (activeView.equals(MyAccountView.class)) {
            setActiveLink(accountLink);
        } else if (activeView.equals(FavoritesView.class)) {
            setActiveLink(favoritesLink);
        }
    }

    private void resetMenuStyles() {
        accountLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#9e0b0b");

        favoritesLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#9e0b0b");
    }

    private void setActiveLink(RouterLink link) {
        link.getStyle()
                .set("background-color", "#e3f2fd")
                .set("font-weight", "bold")
                .set("color", "#1976d2");
    }

    @Override
    public void showRouterLayoutContent(com.vaadin.flow.component.HasElement content) {
        contentArea.getElement().appendChild(content.getElement());
    }
}