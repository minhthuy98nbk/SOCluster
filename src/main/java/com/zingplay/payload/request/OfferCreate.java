package com.zingplay.payload.request;

import com.zingplay.beans.Offer;

import java.util.List;

public class OfferCreate {
    private List<Offer> offers;

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
