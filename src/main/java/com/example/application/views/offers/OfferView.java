package com.example.application.views.offers;


import com.example.application.entity.Apartment;
import com.example.application.services.ApartmentService;
import com.example.application.views.CheckoutFormView;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;


@PageTitle("Oferta")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class OfferView extends Div implements HasUrlParameter<String>, BeforeEnterObserver {
    private Apartment apartment;
    private final ApartmentService apartmentService;

    OfferView(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    private final Button reserve = new Button("Zarezerwuj");

    private void  configListener(){
        reserve.addClickListener(event -> UI.getCurrent().navigate(CheckoutFormView.class, apartment.getUuid()));
    }

    private void configView() {

        addClassNames("checkout-form-view");
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);

        Main content = new Main();
        content.addClassNames(Display.GRID, Gap.XLARGE, AlignItems.START, JustifyContent.CENTER, MaxWidth.SCREEN_MEDIUM,
                Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        content.add(createCheckoutForm());

        add(content);
    }

    private Aside createAside() {
        Aside aside = new Aside();
        aside.addClassNames(Background.CONTRAST_5, BoxSizing.BORDER, Padding.LARGE, BorderRadius.LARGE,
                Position.STICKY);

        UnorderedList ul = new UnorderedList();
        ul.addClassNames(ListStyleType.NONE, Margin.NONE, Padding.NONE, Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM);


        ul.add(createListItem(new Icon(VaadinIcon.USERS), "Maksymalna ilość osób", apartment.getMaxPerson().toString()));
        ul.add(createListItem(new Icon(VaadinIcon.BED), "Ilość łóżek", apartment.getRoomsNumber().toString()));
        ul.add(createListItem(new Icon(VaadinIcon.DOLLAR), "Cena za noc", apartment.getPrice() + " zł"));
        ul.add(createListItem(new Icon(VaadinIcon.MAP_MARKER), "", apartment.getAddress().getCity()));

        aside.add(ul);
        reserve.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Footer headerSection = new Footer();
        headerSection.addClassNames(Display.FLEX, AlignItems.END, JustifyContent.BETWEEN, Margin.Top.XLARGE, Margin.Left.XLARGE);
        headerSection.add(reserve);
        aside.add(headerSection);

        return aside;
    }

    private Section createCheckoutForm() {
        Section checkoutForm = new Section();
        checkoutForm.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW, Grid.Column.COLUMN_SPAN_2);

        H2 header = new H2(apartment.getTitle());
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);

        checkoutForm.add(header, new Paragraph());

        StreamResource imageResource = new StreamResource(apartment.getTitle(),
                () -> getClass().getResourceAsStream(apartment.getImageURL()));
        Image image = new Image(imageResource, apartment.getTitle());
        image.setWidth("70%");


        checkoutForm.add(new HorizontalLayout(image, createAside()));
        checkoutForm.add(new Hr());
        Paragraph description = new Paragraph(apartment.getDescription());
        description.addClassName(LumoUtility.Margin.Vertical.MEDIUM);
        checkoutForm.add(description);
        return checkoutForm;
    }

    private ListItem createListItem(Icon icon, String description, String count) {
        ListItem item = new ListItem();

        item.addClassNames(Display.FLEX, JustifyContent.BETWEEN);

        Div subSection = new Div();
        subSection.addClassNames(Display.FLEX, FlexDirection.COLUMN);

        subSection.add(new Span(icon));
        Span secondarySpan = new Span(description);
        secondarySpan.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subSection.add(secondarySpan);

        Span countSpan = new Span(count);

        item.add(subSection, countSpan);
        return item;
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String param) {
        apartment = apartmentService.getByUuid(param);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        configView();
        configListener();
    }
}
