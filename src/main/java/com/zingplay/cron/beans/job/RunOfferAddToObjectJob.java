package com.zingplay.cron.beans.job;

import com.zingplay.cron.beans.group_jobs.GroupId;
import com.zingplay.models.RunOffer;
import com.zingplay.service.user.ScheduleObjectService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * tao object tu Run offer vao new object or add vao 1 object dang co
 */
public class RunOfferAddToObjectJob extends ACronJob {
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private String idRunOffer;
    private String idObject;
    private RunOffer runOffer;

    public RunOfferAddToObjectJob(long timeStart) {
        super(atomicInteger.getAndIncrement(), GroupId.RUN_OFFER_TO_OBJECT.getCode(), timeStart);
    }

    @Override
    public int getStatus() {
        return runOffer == null ? -1: runOffer.getStatus();
    }

    @Override
    public String getIdJob() {
        return idRunOffer+"_"+idObject;
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
        ScheduleObjectService.getInstance().scanRunOfferToObject(idRunOffer,idObject,runOffer, _getProcessRecord());
    }

    public void setIdRunOffer(String id) {
        this.idRunOffer = id;
    }

    public void setIdObject(String id) {
        idObject = id;
    }

    public RunOffer getRunOffer() {
        return runOffer;
    }

    public void setRunOffer(RunOffer runOffer) {
        this.runOffer = runOffer;
    }
}
