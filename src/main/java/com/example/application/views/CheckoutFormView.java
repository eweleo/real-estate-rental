package com.example.application.views;

import com.example.application.entity.Apartment;
import com.example.application.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.ApartmentService;
import com.example.application.views.offers.OfferViewCard;
import com.example.application.views.offers.OffersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.PermitAll;

import java.time.temporal.ChronoUnit;

@PageTitle("Checkout Form")
@PermitAll
@Route(value = "checkout-form", layout = MainLayout.class)
public class CheckoutFormView extends Div implements HasUrlParameter<String>, BeforeEnterObserver {

    private final DatePicker dateFrom = new DatePicker();
    private final DatePicker dateTo = new DatePicker();
    private final TextField name = new TextField("Imie");
    private final TextField lastName = new TextField("Nazwisko");
    private final EmailField email = new EmailField("Email");
    private final TextField phone = new TextField("Numer telefonu");
    private final TextField cardHolder = new TextField("Właściciel karty");
    private final TextField cardNumber = new TextField("CVV");
    private final TextField securityCode = new TextField("Numer Karty");
    private final Select<String> expirationMonth = new Select<>();
    private final Select<String> expirationYear = new Select<>();
    private Apartment apartment;
    private final ApartmentService apartmentService;
    private final Span counterDays = new Span();
    private final Span totalPrice = new Span();
    private final Button pay = new Button("Zapłać", new Icon(VaadinIcon.LOCK));

    User user;


    public CheckoutFormView(ApartmentService apartmentService, AuthenticatedUser authenticatedUser) {
        this.apartmentService = apartmentService;
        user = authenticatedUser.get().get();
    }

    private void configView() {
        addClassNames("checkout-form-view");
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);

        Main content = new Main();
        content.addClassNames(Display.GRID, Gap.XLARGE, AlignItems.START, JustifyContent.CENTER, MaxWidth.SCREEN_MEDIUM,
                Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        content.add(createCheckoutForm());
        content.add(createAside());
        content.add(new Hr());
        content.add(createFooter());
        add(content);
    }

    private Component createCheckoutForm() {
        Section checkoutForm = new Section();
        checkoutForm.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW);

        H2 header = new H2("Rezerwacja");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        checkoutForm.add(header);
        checkoutForm.add(new OfferViewCard(apartment));
        checkoutForm.add(createShippingAddressSection());
        checkoutForm.add(createPersonalDetailsSection());
        checkoutForm.add(createPaymentInformationSection());

        return checkoutForm;
    }

    private Section createPersonalDetailsSection() {
        Section personalDetails = new Section();
        personalDetails.addClassNames(Display.FLEX, FlexDirection.COLUMN, Margin.Bottom.XLARGE, Margin.Top.MEDIUM);


        H3 header = new H3("Dane");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        name.setRequiredIndicatorVisible(true);
        name.setPattern("[\\p{L} \\-]+");
        name.addClassNames(Margin.Bottom.SMALL);
        name.setRequired(true);
        name.setValue(user.getFirstName());

        lastName.setRequiredIndicatorVisible(true);
        lastName.setPattern("[\\p{L} \\-]+");
        lastName.addClassNames(Margin.Bottom.SMALL);
        lastName.setRequired(true);
        lastName.setValue(user.getLastName());

        email.setRequiredIndicatorVisible(true);
        email.addClassNames(Margin.Bottom.SMALL);
        email.setRequired(true);
        email.setValue(user.getEmail());

        phone.setRequiredIndicatorVisible(true);
        phone.setPattern("[\\d \\-\\+]+");
        phone.addClassNames(Margin.Bottom.SMALL);
        phone.setRequired(true);

        personalDetails.add(header, name, lastName, email, phone);
        return personalDetails;
    }

    private Section createShippingAddressSection() {
        Section shippingDetails = new Section();
        shippingDetails.addClassNames(Display.FLEX, FlexDirection.COLUMN, Margin.Bottom.XLARGE, Margin.Top.MEDIUM);

        H3 header = new H3("Data pobytu");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        Div subSection = new Div();
        subSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);
        dateFrom.setPlaceholder("Data zameldowania");
        dateFrom.setRequired(true);
        dateTo.setPlaceholder("Data wymeldowania");
        dateTo.setRequired(true);

        subSection.add(dateFrom, dateTo);

        shippingDetails.add(header, subSection);
        return shippingDetails;
    }

    private Component createPaymentInformationSection() {
        Section paymentInfo = new Section();
        paymentInfo.addClassNames(Display.FLEX, FlexDirection.COLUMN, Margin.Bottom.XLARGE, Margin.Top.MEDIUM);

        H3 header = new H3("Płatność");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        cardHolder.setRequiredIndicatorVisible(true);
        cardHolder.setPattern("[\\p{L} \\-]+");
        cardHolder.addClassNames(Margin.Bottom.SMALL);
        cardHolder.setRequired(true);

        Div subSectionOne = new Div();
        subSectionOne.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        cardNumber.setRequiredIndicatorVisible(true);
        cardNumber.setPattern("[0-9]{3,4}");
        cardNumber.addClassNames(Margin.Bottom.SMALL);
        cardNumber.setRequired(true);

        securityCode.setRequiredIndicatorVisible(true);
        securityCode.setPattern("[\\d ]{12,23}");
        securityCode.addClassNames(Flex.GROW, Margin.Bottom.SMALL);
        securityCode.setRequired(true);

        subSectionOne.add(securityCode, cardNumber);

        Div subSectionTwo = new Div();
        subSectionTwo.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        expirationMonth.setLabel("Miesiąc");
        expirationMonth.setRequiredIndicatorVisible(true);
        expirationMonth.setItems("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

        expirationYear.setLabel("Rok");
        expirationYear.setRequiredIndicatorVisible(true);
        expirationYear.setItems("24", "25", "26", "27", "28");

        subSectionTwo.add(expirationMonth, expirationYear);

        paymentInfo.add(header, cardHolder, subSectionOne, subSectionTwo);
        return paymentInfo;
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Margin.Vertical.MEDIUM);

        Button cancel = new Button("Anuluj");
        cancel.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(OffersView.class));
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        footer.add(cancel, pay);
        return footer;
    }

    private Aside createAside() {
        Aside aside = new Aside();
        aside.addClassNames(Background.CONTRAST_5, BoxSizing.BORDER, Padding.LARGE, BorderRadius.LARGE,
                Position.STICKY);
        Header headerSection = new Header();
        headerSection.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Margin.Bottom.MEDIUM);
        H3 header = new H3("Podsumowanie");
        header.addClassNames(Margin.NONE);

        headerSection.add(header);

        UnorderedList ul = new UnorderedList();
        ul.addClassNames(ListStyleType.NONE, Margin.NONE, Padding.NONE, Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM);

        ul.add(createListItem(apartment.getTitle(), " "));


        aside.add(headerSection, ul);
        return aside;
    }

    private ListItem createListItem(String primary, String secondary) {
        ListItem item = new ListItem();
        item.addClassNames(Display.FLEX, JustifyContent.BETWEEN);

        Div subSection = new Div();
        subSection.addClassNames(Display.FLEX, FlexDirection.COLUMN);

        subSection.add(new Span(primary));
        Span secondarySpan = new Span(secondary);
        secondarySpan.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subSection.add(secondarySpan);


        item.add(subSection, counterDays, totalPrice);
        return item;
    }

    private void setNumberDays() {
        if (!dateFrom.isEmpty() && !dateTo.isEmpty() && dateFrom.getValue().isBefore(dateTo.getValue())) {
            int days = (int) ChronoUnit.DAYS.between(dateFrom.getValue(), dateTo.getValue());
            counterDays.setVisible(true);
            counterDays.setText(days + " dni");
            totalPrice.setVisible(true);
            totalPrice.setText(days * apartment.getPrice() + " zł");
        } else {
            counterDays.setVisible(false);
            totalPrice.setVisible(false);
        }
    }

    private void configListener() {
        dateFrom.addValueChangeListener(e -> setNumberDays());
        dateTo.addValueChangeListener(e -> setNumberDays());
        pay.addClickListener(e -> {
            if (dateFrom.isEmpty() || dateTo.isEmpty() || name.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || cardHolder.isEmpty() || cardNumber.isEmpty() || securityCode.isEmpty() || expirationMonth.isEmpty() || expirationYear.isEmpty()) {
                Dialog dialog = new Dialog("Rezerwacja");
                Span span = new Span("Wypełnij dane");
                Button ok = new Button("OK", buttonClickEvent -> dialog.close());
                ok.addThemeVariants(ButtonVariant.LUMO_ERROR);
                dialog.add(new VerticalLayout(span, ok));
                dialog.open();
            } else {
                Dialog dialog = new Dialog("Rezerwacja");
                Span span = new Span("Transakcja została sfinalizowana, szczegóły dostaniesz na maila");
                Button ok = new Button("OK", buttonClickEvent -> {
                    dialog.close();
                    UI.getCurrent().navigate(OffersView.class);
                });
                ok.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                dialog.add(new VerticalLayout(span, ok));
                dialog.open();
            }
        });
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param) {
        apartment = apartmentService.getByUuid(param);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        configView();

        configListener();
    }
}