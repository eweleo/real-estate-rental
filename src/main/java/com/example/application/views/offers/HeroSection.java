package com.example.application.views.offers;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;

public class HeroSection extends Div {

    private static final String TITLE = "Znajdź idealne miejsce na pobyt";
    private static final String SUBTITLE = "Przeglądaj najlepsze oferty wynajmu w Twojej okolicy";

    public HeroSection() {
        configureHero();
    }

    private void configureHero() {
        setHeroStyles();
        add(createTitle(), createSubtitle());
    }

    private void setHeroStyles() {
        getStyle()
                .set("background", "linear-gradient(135deg, #7c8ff5 0%, #9a82db 100%)")
                .set("color", "white")
                .set("padding", "60px 20px")
                .set("text-align", "center")
                .set("width", "100%")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.08)");
    }

    private H1 createTitle() {
        H1 title = new H1(TITLE);
        title.getStyle()
                .set("margin", "0 0 15px 0")
                .set("font-size", "38px")
                .set("font-weight", "600")
                .set("letter-spacing", "-0.5px")
                .set("color", "white");
        return title;
    }

    private Span createSubtitle() {
        Span subtitle = new Span(SUBTITLE);
        subtitle.getStyle()
                .set("font-size", "17px")
                .set("opacity", "0.95")
                .set("font-weight", "400");
        return subtitle;
    }
}