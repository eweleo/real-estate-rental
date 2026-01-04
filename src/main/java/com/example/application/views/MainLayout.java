package com.example.application.views;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.account.MyAccountView;
import com.example.application.views.apartment.MyApartmentsView;
import com.example.application.views.offers.OffersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class MainLayout extends AppLayout {

    private static final String BANNER_URL = "/META-INF/resources/icons/baner.png";
    private final AuthenticatedUser authenticatedUser;

    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        addToNavbar(createHeaderContent());
        setDrawerOpened(false);
    }

    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);
        layout.getStyle()
                .set("background-color", "#ffffff")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.08)")
                .set("padding", "15px 30px");

        RouterLink appLink = createAppLink();
        layout.add(appLink);

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            layout.add(createUserMenu(maybeUser.get()));
        } else {
            layout.add(createGuestMenu());
        }

        Nav nav = createNavigation();
        Image banner = createBanner();

        header.add(layout, nav, new HorizontalLayout(banner));
        return header;
    }

    private RouterLink createAppLink() {
        RouterLink link = new RouterLink("Nieruchomości", OffersView.class);
        link.getStyle()
                .set("text-decoration", "none")
                .set("color", "#667eea")
                .set("font-size", "28px")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.5px")
                .set("margin-right", "auto")
                .set("transition", "color 0.3s");

        link.getElement().addEventListener("mouseenter", e ->
                link.getStyle().set("color", "#764ba2"));

        link.getElement().addEventListener("mouseleave", e ->
                link.getStyle().set("color", "#667eea"));

        return link;
    }

    private MenuBar createUserMenu(User user) {
        Avatar avatar = createAvatar(user);

        MenuBar userMenu = new MenuBar();
        userMenu.setThemeName("tertiary-inline contrast");

        MenuItem userName = userMenu.addItem("");
        Div menuContent = createMenuContent(user, avatar);
        userName.add(menuContent);

        userName.getSubMenu().addItem("Moje konto", e -> {
            UI.getCurrent().navigate(MyAccountView.class);
        });

        if (user.isLandlord()) {
            userName.getSubMenu().addItem("Moje nieruchomości", e -> {
                UI.getCurrent().navigate(MyApartmentsView.class);
            });
        }

        userName.getSubMenu().addItem("Wyloguj się", e -> {
            authenticatedUser.logout();
        });

        return userMenu;
    }

    private Avatar createAvatar(User user) {
        Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
        }
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");
        return avatar;
    }

    private Div createMenuContent(User user, Avatar avatar) {
        Div div = new Div();
        div.add(avatar);

        Span userName = new Span(user.getFirstName() + " " + user.getLastName());
        userName.getStyle()
                .set("font-weight", "500")
                .set("color", "#333");
        div.add(userName);

        Icon dropdownIcon = new Icon("lumo", "dropdown");
        dropdownIcon.getStyle().set("color", "#666");
        div.add(dropdownIcon);

        div.getElement().getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-s)");
        return div;
    }

    private Div createGuestMenu() {
        Anchor listProperty = new Anchor("register-landlord", "Zarejestruj się jako wynajmujący");
        listProperty.getStyle()
                .set("text-decoration", "none")
                .set("color", "#667eea")
                .set("font-weight", "500")
                .set("padding", "8px 16px")
                .set("border-radius", "6px")
                .set("transition", "all 0.3s");

        listProperty.getElement().addEventListener("mouseenter", e ->
                listProperty.getStyle().set("background-color", "#f0f4ff"));
        listProperty.getElement().addEventListener("mouseleave", e ->
                listProperty.getStyle().set("background-color", "transparent"));

        Anchor signUp = new Anchor("register", "Zarejestruj się");
        signUp.getStyle()
                .set("text-decoration", "none")
                .set("color", "#333")
                .set("font-weight", "500")
                .set("padding", "8px 16px")
                .set("border-radius", "6px")
                .set("transition", "all 0.3s");

        signUp.getElement().addEventListener("mouseenter", e ->
                signUp.getStyle().set("background-color", "#f5f5f5"));
        signUp.getElement().addEventListener("mouseleave", e ->
                signUp.getStyle().set("background-color", "transparent"));

        Anchor signIn = new Anchor("login", "Zaloguj się");
        signIn.getStyle()
                .set("text-decoration", "none")
                .set("color", "white")
                .set("font-weight", "600")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("transition", "all 0.3s")
                .set("box-shadow", "0 2px 4px rgba(102, 126, 234, 0.3)");

        signIn.getElement().addEventListener("mouseenter", e ->
                signIn.getStyle().set("transform", "translateY(-2px)")
                        .set("box-shadow", "0 4px 8px rgba(102, 126, 234, 0.4)"));
        signIn.getElement().addEventListener("mouseleave", e ->
                signIn.getStyle().set("transform", "translateY(0)")
                        .set("box-shadow", "0 2px 4px rgba(102, 126, 234, 0.3)"));

        Div guestMenu = new Div(listProperty, signUp, signIn);
        guestMenu.getElement().getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "10px");

        return guestMenu;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);

        return nav;
    }

    private Image createBanner() {
        StreamResource imageResource = new StreamResource("baner.png",
                () -> getClass().getResourceAsStream(BANNER_URL));
        Image image = new Image(imageResource, "baner");
        image.setWidth("100%");
        image.getStyle().set("display", "block");
        return image;
    }
}