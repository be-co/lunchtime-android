package com.tbaehr.lunchtime.model;

import java.util.List;

/**
 * Created by timo.baehr@gmail.com on 08.01.17.
 */
public class Offers {

    private String title;

    private String description;

    private List<Offer> offers;

    public Offers(String title, String description, List<Offer> offers) {
        this.title = title;
        this.description = description;
        this.offers = offers;
    }

    public String getRestaurantName() {
        return title;
    }

    public String getRestaurantDescription() {
        return description;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public boolean isEmpty() {
        return offers.isEmpty();
    }
}
