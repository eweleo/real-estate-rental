package com.example.application.views;

import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.account.MyAccountView;
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
        link.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.XXXLARGE, FontWeight.EXTRABOLD);
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

        userName.getSubMenu().addItem("Wyloguj się", e -> {
            authenticatedUser.logout();
        });

        return userMenu;
    }

    private Avatar createAvatar(User user) {
        Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        StreamResource resource = new StreamResource("profile-pic",
                () -> new ByteArrayInputStream(user.getProfilePicture()));
        avatar.setImageResource(resource);
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");
        return avatar;
    }

    private Div createMenuContent(User user, Avatar avatar) {
        Div div = new Div();
        div.add(avatar);
        div.add(user.getFirstName() + " " + user.getLastName());
        div.add(new Icon("lumo", "dropdown"));
        div.getElement().getStyle().set("display", "flex");
        div.getElement().getStyle().set("align-items", "center");
        div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
        return div;
    }

    private Div createGuestMenu() {
        Anchor listProperty = new Anchor("list your property", "Udostępnij obiekt");
        Anchor signUp = new Anchor("sign up", "Zarejestruj się");
        Anchor signIn = new Anchor("login", "Zaloguj się");

        Div guestMenu = new Div(listProperty, signUp, signIn);
        guestMenu.getElement().getStyle().set("display", "flex");
        guestMenu.getElement().getStyle().set("align-items", "center");
        guestMenu.getElement().getStyle().set("gap", "var(--lumo-space-l)");

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
        return image;
    }
}