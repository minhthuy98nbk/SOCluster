package com.zingplay.module.report.v2;

import com.zingplay.helpers.Helpers;
import com.zingplay.models.Offer;
import com.zingplay.models.RunOffer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "report_offer_daily")
public class ReportOfferDaily {
    @Id
    private String id;
    //phan chung -> xac dinh
    private String game;
    private String country;
    private String date;

    //phan rieng
    private String name;
    @Indexed
    @DBRef
    private RunOffer runOffer;
    private Set<ReportOffer> report;

    @CreatedDate
    private Date timeCreate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RunOffer getRunOffer() {
        return runOffer;
    }

    public void setRunOffer(RunOffer runOffer) {
        this.runOffer = runOffer;
    }

    public Set<ReportOffer> getReport() {
        if(report == null) {
            report = new HashSet<>();
        }
        return report;
    }

    public void setReport(Set<ReportOffer> report) {
        this.report = report;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    void addUserBuyOffer(String country, float price) {
        Set<ReportOffer> report = this.getReport();
        ReportOffer r = null;
        for (ReportOffer reportOffer : report) {
            if(reportOffer.getCountry().equals(country)){
                r = reportOffer;
                break;
            }
        }
        if(r == null){
            r = new ReportOffer();
            r.setCountry(country);
            r.setRev(0);
            r.setUsers(0);
            Offer offer = runOffer.getOffer();
            String currency = Helpers.getCurrencyFrom(offer, country);
            r.setCurrency(currency);
            report.add(r);
            setReport(report);
        }
        r.setRev(r.getRev() + price);
        r.setUsers(r.getUsers() + 1);
    }
}
