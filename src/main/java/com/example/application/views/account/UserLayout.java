package com.example.application.views.account;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.MainLayout;
import com.example.application.views.apartment.MyApartmentsView;
import com.example.application.views.transaction.TransactionHistoryView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@ParentLayout(MainLayout.class)
public class UserLayout extends Div implements RouterLayout, BeforeEnterObserver {

    private final User currentUser;

    private RouterLink accountLink;
    private RouterLink favoritesLink;
    private RouterLink historyLink;
    private RouterLink paymentLink;
    private RouterLink myApartmentsLink;
    private RouterLink changePasswordLink;
    private Div contentArea;

    public UserLayout(AuthenticatedUser authenticatedUser) {
        this.currentUser = authenticatedUser.get().orElseThrow();
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
        historyLink = createMenuLink("Historia Transakcji", TransactionHistoryView.class);
        paymentLink = createMenuLink("Metody Płatności", PaymentMethodsView.class);
        myApartmentsLink = createMenuLink("Moje obiekty", MyApartmentsView.class);
        changePasswordLink = createMenuLink("Zmień hasło",ChangePasswordView.class);


        menu.add(menuTitle, accountLink,changePasswordLink,myApartmentsLink, favoritesLink, historyLink, paymentLink);

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
                .set("color", "#333")
                .set("border-radius", "6px")
                .set("margin", "8px 0")
                .set("transition", "all 0.2s")
                .set("font-weight", "500")
                .set("font-size", "15px");

        link.getElement().addEventListener("mouseenter", e -> {
            String bgColor = link.getStyle().get("background-color");
            if (bgColor == null || !bgColor.contains("227, 242, 253")) { // #e3f2fd w RGB
                link.getStyle()
                        .set("background-color", "#f5f5f5")
                        .set("color", "#667eea");
            }
        });

        link.getElement().addEventListener("mouseleave", e -> {
            String bgColor = link.getStyle().get("background-color");
            if (bgColor == null || !bgColor.contains("227, 242, 253")) {
                link.getStyle()
                        .set("background-color", "transparent")
                        .set("color", "#333");
            }
        });

        return link;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        resetMenuStyles();

        Class<?> activeView = event.getNavigationTarget();
        if (activeView.equals(MyAccountView.class)) {
            setActiveLink(accountLink);
        } else if (activeView.equals(ChangePasswordView.class)) {
            setActiveLink(changePasswordLink);
        } else if (activeView.equals(MyApartmentsView.class)) {
            setActiveLink(myApartmentsLink);
        } else if (activeView.equals(FavoritesView.class)) {
            setActiveLink(favoritesLink);
        } else if (activeView.equals(TransactionHistoryView.class)) {
            setActiveLink(historyLink);
        } else if (activeView.equals(PaymentMethodsView.class)) {
            setActiveLink(paymentLink);
        }

        if(!currentUser.isLandlord()){
            myApartmentsLink.setVisible(false);
        }
    }

    private void resetMenuStyles() {
        accountLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#333");

        changePasswordLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#333");

        myApartmentsLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#333");

        favoritesLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#333");

        historyLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#333");

        paymentLink.getStyle()
                .set("background-color", "transparent")
                .set("font-weight", "500")
                .set("color", "#333");
    }

    private void setActiveLink(RouterLink link) {
        link.getStyle()
                .set("background-color", "#e3f2fd")
                .set("font-weight", "600")
                .set("color", "#667eea");
    }

    @Override
    public void showRouterLayoutContent(com.vaadin.flow.component.HasElement content) {
        contentArea.getElement().appendChild(content.getElement());
    }
}