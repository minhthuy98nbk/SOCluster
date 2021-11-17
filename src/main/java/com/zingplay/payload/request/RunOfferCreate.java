package com.zingplay.payload.request;

import com.zingplay.beans.RunOffer;

import java.util.List;

public class RunOfferCreate {
    private List<RunOffer> runOffers;

    public List<RunOffer> getRunOffers() {
        return runOffers;
    }

    public void setRunOffers(List<RunOffer> RunOffers) {
        this.runOffers = RunOffers;
    }
}
