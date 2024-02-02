package com.example.application.views.offers;

import com.example.application.entity.Apartment;
import com.example.application.services.ApartmentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import java.util.List;

@PageTitle("Oferty")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class OffersView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final ApartmentService apartmentService;
    private final DatePicker dateFrom = new DatePicker();
    private final DatePicker dataTo = new DatePicker();
    private final ComboBox cityBox = new ComboBox<>();

    public OffersView(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
//        HorizontalLayout layoutRow = new HorizontalLayout();
//
//        HorizontalLayout layoutRow2 = new HorizontalLayout();
//        VerticalLayout layoutColumn2 = new VerticalLayout();
//        VerticalLayout layoutColumn3 = new VerticalLayout();
//        getContent().setWidth("100%");
//        getContent().getStyle().set("flex-grow", "1");
//        layoutRow2.addClassName(Gap.MEDIUM);
//        layoutRow2.setWidth("100%");
//        layoutRow2.setHeight("min-content");
//        layoutRow.addClassName(Gap.MEDIUM);
//        layoutRow.setWidth("100%");
//        layoutRow.getStyle().set("flex-grow", "1");
//        layoutColumn2.setWidth("100%");
//        layoutColumn2.getStyle().set("flex-grow", "1");
//        layoutColumn3.getStyle().set("flex-grow", "1");
//        getContent().add(layoutRow2);
//        getContent().add(layoutRow);
//        layoutRow.add(layoutColumn2);
//        layoutRow.add(layoutColumn3);
//        layoutRow.add(apartment(),apartment(),apartment());
    }

    Div apartment (){
        Div div = new Div();
        Apartment apartment = apartmentService.findAll().get(0);
        H4 title = new H4(apartment.getTitle());
        div.add(title);
        StreamResource imageResource = new StreamResource("zdjecie",
                () -> getClass().getResourceAsStream(apartment.getImageURL()));
        Image image = new Image(imageResource,"zdjecie");
        image.setMaxWidth("40%");
        image.setMaxHeight("40%");
        div.add(image);
        return div;
    }

    private void configureView() {
        Header header = new Header();
        HorizontalLayout layoutRow = new HorizontalLayout();


        dateFrom.setPlaceholder("Data zameldowania");
        dataTo.setPlaceholder("Data wymeldowania");
        configureComboBoxCity();

        Div dateLayout = new Div(dateFrom,new Text(" - "), dataTo);


        NumberField countPerson = new NumberField();
        countPerson.setStep(1);
        countPerson.setStepButtonsVisible(true);
        countPerson.setPlaceholder("Ilość osób");

        layoutRow.add(cityBox,dateLayout,countPerson);

        layoutRow.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutRow.setBoxSizing(BoxSizing.BORDER_BOX);

        header.add(layoutRow);
        getContent().addAndExpand(header, new HorizontalLayout(), new HorizontalLayout());

        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        layoutRow.setClassName("color-green");





    }

    private void configureComboBoxCity(){
        cityBox.setItems(List.of("Łódź","Warszawa","Kraków"));
        cityBox.isClearButtonVisible();
        cityBox.setPlaceholder("Dokąd się wybierasz");
    }

    private void test(){
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));
        getContent().add(new HorizontalLayout(apartment(),apartment(),apartment()));

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        configureView();
        test();
    }
}
