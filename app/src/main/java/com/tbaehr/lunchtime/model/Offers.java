package com.tbaehr.lunchtime.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Set<Date> getUiRefreshDates() {
        Set<Date> refreshDates = new HashSet<>();
        for (Offer offer : getOffers()) {
            boolean soonValid = offer.getValidationState().equals(Offer.ValidationState.SOON_VALID);
            boolean nowValid = offer.getValidationState().equals(Offer.ValidationState.NOW_VALID);
            if (soonValid) {
                refreshDates.add(offer.getStartDate());
            }
            if (nowValid) {
                refreshDates.add(offer.getEndDate());
            }
        }
        return refreshDates;
    }
}
