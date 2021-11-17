package com.zingplay.cron;

import com.zingplay.beans.TimeSchedule;
import com.zingplay.models.RunOffer;
import com.zingplay.service.user.ScheduleObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class CronBackup {
    private static final Logger log = LoggerFactory.getLogger(CronBackup.class);

    private final ScheduleObjectService scheduleObjectService;
    private Date nextTimeScan;
    private boolean needScanRunOffer;


    @Autowired
    public CronBackup(@Lazy ScheduleObjectService scheduleObjectService) {
        this.scheduleObjectService = scheduleObjectService;
        needScanRunOffer = false;
    }
    //@EventListener
    public void startAuto(ApplicationReadyEvent event) throws InterruptedException {
        //add all consumer run
        log.info("startAuto check scan object, run offer...");
        nextTimeScan = _findNextTimeScanIfHad();
        needScanRunOffer = _findRunOfferNeedScan();
    }

    private boolean _findRunOfferNeedScan() {
        return scheduleObjectService.needScanRunOffer();
    }

    //@Scheduled(cron = "")//hen gio chinh xac thuc hien
    //@Scheduled(initialDelay = 0)//thoi gian cho truoc khi thuc hien
    //@Scheduled(fixedDelay = 0)//sau khi thuc hien xong thi delay bao lau
    //@Scheduled(fixedRate = 0)//bao lau thi chay 1 lan
    //@Scheduled(initialDelay = 1000, fixedDelay = 5000)
    //@Scheduled(initialDelay = 5000)
    public void cacheRefresh(){
        _checkScanObject();
        _checkScanRunOffer();
    }
    private void _checkScanObject() {
        if(nextTimeScan == null) {
            return;
        }
        Date curTime = new Date();
        if(curTime.after(nextTimeScan)){
            //scan
            String s = scheduleObjectService.scanObject(curTime);
            if(s != null){
                needScanRunOffer = true;
            }

            //update nextTimeScan
            nextTimeScan = _findNextTimeScanIfHad();
        }
    }
    private Date _findNextTimeScanIfHad() {
        Date curTime = new Date();
        curTime.setTime(curTime.getTime() - 1000);
        return scheduleObjectService.getTimeScanNextIfHad(curTime);
    }
    private void _checkScanRunOffer() {
        if(!needScanRunOffer){ return; }
        needScanRunOffer = scheduleObjectService.scanRunOffer();
    }

    public void addScanRunOffer() {
        needScanRunOffer = true;
    }
    public void addScanRunOffer(List<RunOffer> saveAll) {
        needScanRunOffer = true;
    }
    public void startScanObject(Date timeScan){
        if(nextTimeScan!= null){
            if(nextTimeScan.after(timeScan)){
                nextTimeScan = timeScan;
            }
        }else{
            nextTimeScan = timeScan;
        }
    }
    public void startScanObject(Set<Date> times) {
        Date minTimeScan = null;

        for (Date time : times) {
            if(minTimeScan == null){
                minTimeScan = time;
            }else{
                if (minTimeScan.after(time)) {
                    minTimeScan = time;
                }
            }
        }
        startScanObject(minTimeScan);
    }
    public void startScanObjectVia(Set<TimeSchedule> times) {
        Date minTimeScan = null;

        for (TimeSchedule timeSchedule : times) {
            Date time = timeSchedule.getTimeScan();
            if(minTimeScan == null){
                minTimeScan = time;
            }else{
                if (minTimeScan.after(time)) {
                    minTimeScan = time;
                }
            }
        }
        startScanObject(minTimeScan);
    }
}
