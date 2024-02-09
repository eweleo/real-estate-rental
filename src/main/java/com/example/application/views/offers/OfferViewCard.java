package com.example.application.views.offers;

import com.example.application.entity.Apartment;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class OfferViewCard extends ListItem {
    public OfferViewCard(Apartment apartment) {
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(LumoUtility.Background.CONTRAST, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
        div.setHeight("160px");

        StreamResource imageResource = new StreamResource(apartment.getTitle(),
                () -> getClass().getResourceAsStream(apartment.getImageURL()));
        Image image = new Image(imageResource, apartment.getTitle());
        image.setWidth("100%");

        div.add(image);

        Span header = new Span();
        header.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
        header.setText(apartment.getTitle());

        Span subtitle = new Span();
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        subtitle.setText(apartment.getPrice() + "zł/ dzień");

        Paragraph description = new Paragraph(
                apartment.getDescription().substring(0, 200) + "...");
        description.addClassName(LumoUtility.Margin.Vertical.MEDIUM);

        Span badge = new Span();
        badge.getElement().setAttribute("theme", "badge");
        badge.setText("Więcej");

        add(div, header, subtitle, description, badge);

        addClickListener(listItemClickEvent -> {
            System.out.println(apartment.getUuid());
            UI.getCurrent().navigate(OfferView.class, apartment.getUuid());

        });
    }
}
