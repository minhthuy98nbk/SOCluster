package com.zingplay.cron.beans.job;

import com.zingplay.cron.beans.group_jobs.GroupId;
import com.zingplay.models.RunOffer;
import com.zingplay.service.user.ScheduleObjectService;

import java.util.concurrent.atomic.AtomicInteger;

public class RunOfferJob extends ACronJob {
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private String idRunOffer;
    private RunOffer runOffer;

    public RunOfferJob(long timeStart) {
        super(atomicInteger.getAndIncrement(), GroupId.RUN_OFFER.getCode(), timeStart);
    }

    @Override
    public int getStatus() {
        return runOffer == null ? -1: runOffer.getStatus();
    }

    @Override
    public String getIdJob() {
        return idRunOffer;
    }

    @Override
    public String getGame() {
        return runOffer.getGame();
    }

    @Override
    public String getCountry() {
        return runOffer.getCountry();
    }

    @Override
    protected void doJob() {
        ScheduleObjectService.getInstance().scanRunOffer(idRunOffer,runOffer);
    }

    public void setIdRunOffer(String id) {
        this.idRunOffer = id;
    }

    public RunOffer getRunOffer() {
        return runOffer;
    }

    public void setRunOffer(RunOffer runOffer) {
        this.runOffer = runOffer;
    }
}
