package com.zingplay.service.user;

import com.zingplay.constant.DateTimeConstant;
import com.zingplay.cron.CronJobServiceImpl;
import com.zingplay.cron.beans.job.RunOfferJob;
import com.zingplay.cron.beans.job.ScanUserGroupJob;
import com.zingplay.cron.exception.CronJobException;
import com.zingplay.enums.Config;
import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogErrorAction;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.log.LogSystemAction;
import com.zingplay.models.Object;
import com.zingplay.models.*;
import com.zingplay.module.objects.ConditionController;
import com.zingplay.module.objects.ConditionGame;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.*;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
public class ScheduleObjectService {

    private final ObjectRepository objectRepository;
    private final ScheduleObjectRepository scheduleObjectRepository;
    private final UserRepository userRepository;
    private final UserBuyOfferRepository userBuyOfferRepository;
    private final UserObjectRepository userObjectRepository;
    private final RunOfferRepository runOfferRepository;
    private final CustomDataService customDataService;
    private final Helpers helpers;
    private final LogService logService;
    private static ScheduleObjectService _instance;


    public static ScheduleObjectService getInstance(){
        return _instance;
    }

    @Autowired
    public ScheduleObjectService(ObjectRepository objectRepository, ScheduleObjectRepository scheduleObjectRepository, UserRepository userRepository, @Lazy UserBuyOfferRepository userBuyOfferRepository, UserObjectRepository userObjectRepository, RunOfferRepository runOfferRepository, CustomDataService customDataService, Helpers helpers, LogService logService) {
        this.objectRepository = objectRepository;
        this.scheduleObjectRepository = scheduleObjectRepository;
        this.userRepository = userRepository;
        this.userBuyOfferRepository = userBuyOfferRepository;
        this.userObjectRepository = userObjectRepository;
        this.runOfferRepository = runOfferRepository;
        this.customDataService = customDataService;
        this.helpers = helpers;
        this.logService = logService;
        _instance = this;
    }

    public Page<?> getAllScheduleObject(String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(search!=null && !search.isEmpty()){
            Page<Object> byIdObjectContainingAndGameAndCountry = objectRepository.findByIdObjectContaining(search, pageable);
            List<Object> content = byIdObjectContainingAndGameAndCountry.getContent();
            return scheduleObjectRepository.findByObjectIsInAndGameAndCountry(content, game, country, pageable);
        }
        return scheduleObjectRepository.findByGameAndCountry(game, country, pageable);
    }

    public Page<?> getAllScheduleObjectDetail(String id, String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(search!=null && !search.isEmpty()){
            return scheduleObjectRepository.findByNameContainingAndGameAndCountryAndObject_Id(search, game, country, id, pageable);
        }
        return scheduleObjectRepository.findByGameAndCountryAndObject_Id(game, country, id, pageable);
    }

    public ResponseEntity<?> create(com.zingplay.beans.ScheduleObject scheduleObject) {
        Date timeScan = scheduleObject.getTimeScan();
        if(timeScan == null){
            return ResponseEntity.badRequest().body(new MessageResponse("TimeScan is empty!!"));
        }
        String name = scheduleObject.getName();
        if(name == null || name.isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponse("Name is empty!!"));
        }
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        LogGameDesignAction.getInstance().info("createScheduleObject creating|{}|{}|{}|{}" , username, game, country, scheduleObject.getIdObject());
        com.zingplay.models.Object object1 = objectRepository.findFirstByIdObject(scheduleObject.getIdObject()).orElse(null);
        if(object1 == null){
            LogGameDesignAction.getInstance().info("createScheduleObject create failed object was exist|{}|{}|{}|{}" , username, game, country, scheduleObject.getIdObject());
            return ResponseEntity.badRequest().body(new MessageResponse("Object not exists!!"));
        }
        ScheduleObject schedule = new ScheduleObject();
        schedule.setObject(object1);
        schedule.setName(name);
        schedule.setTimeScan(timeScan);
        schedule.setGame(game);
        schedule.setCountry(country);
        schedule.setStatus(Config.WAIT_SCAN);
        ScheduleObject save = scheduleObjectRepository.save(schedule);
        LogGameDesignAction.getInstance().info("createScheduleObject created|{}|{}|{}|{}" , username, game, country, scheduleObject.getIdObject());
        logService.addLog("create schedule object [" + object1.getIdObject() +"] " + schedule.getTimeScan());
        addCronScanObject(save);
        //cron.startScanObject(save.getTimeScan());
        return ResponseEntity.ok().body(save);
    }
    private void addCronScanObject(ScheduleObject save) {
        com.zingplay.models.Object object = save.getObject();
        String objectId  = "#";
        if(object!= null){
            objectId = object.getIdObject();
        }
        LogSystemAction.getInstance().info("addCronScanObject|" + objectId + "|" + save.getTimeScan());

        ScanUserGroupJob job = new ScanUserGroupJob(save.getTimeScan().getTime());
        job.setScheduleObject(save);
        job.setIdScheduleObject(save.getId());
        try {
            CronJobServiceImpl.getInstance().addJob(job, false);
        } catch (CronJobException e) {
            LogSystemAction.getInstance().error("addCronScanObject exception|" + objectId + "|" + save.getTimeScan());
            e.printStackTrace();
        }
    }

    public void createScheduleObjectSequence(String idScheduleObject, ScheduleObject oldSchedule){
        com.zingplay.models.Object  object = oldSchedule.getObject();

        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(oldSchedule.getGame(), oldSchedule.getCountry());

        object = object == null? null : objectRepository.findFirstByIdObjectAndGameAndCountry(object.getIdObject(), oldSchedule.getGame(), oldSchedule.getCountry()).orElse(null);
        if(object == null || object.getScheduleFrequency() <= 0){
            scheduleObjectRepository.delete(oldSchedule);
            MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
            return;
        }
        long timeExtend = ((long) object.getScheduleFrequency()) * ((long) DateTimeConstant.MILLIS_PER_DAY);
        Date newTimeScan = new Date(oldSchedule.getTimeScan().getTime() + timeExtend);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(newTimeScan);
        ScheduleObject schedule = new ScheduleObject();
        schedule.setObject(object);
        schedule.setName(object.getIdObject()+"_schedule_"+object.getScheduleFrequency()+"_"+strDate);
        schedule.setTimeScan(newTimeScan);
        schedule.setStatus(Config.WAIT_SCAN);
        schedule.setGame(object.getGame());
        schedule.setCountry(object.getCountry());

        ScheduleObject save = scheduleObjectRepository.save(schedule);
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();

        addCronScanObject(save);
    }

    public ScheduleObject updateScheduleObject(String id, com.zingplay.beans.ScheduleObject object){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        LogGameDesignAction.getInstance().info("updateScheduleObject updating|{}|{}|{}|{}" , username, game, country, object.getIdObject());
        return scheduleObjectRepository.findById(id).map(scheduleObject -> {
            LogGameDesignAction.getInstance().info("updateScheduleObject updated|{}|{}|{}|{}" , username, game, country, object.getIdObject());
            logService.addLog("update schedule object [" + object.getName() +"] " + scheduleObject.getTimeScan());
            scheduleObject.setName(object.getName());
            scheduleObject.setTimeScan(object.getTimeScan());
            if(scheduleObject.getStatus() != Config.WAIT_SCAN){
                scheduleObject.setStatus(Config.WAIT_RESCAN);
            }
            ScheduleObject save = scheduleObjectRepository.save(scheduleObject);
            addCronScanObject(save);
            //cron.startScanObject(save.getTimeScan());
            return save;
        }).orElse(null);
    }

    public void deleteScheduleObject(String id){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        ScheduleObject scheduleObject = scheduleObjectRepository.findById(id).orElse(null);
        if(scheduleObject != null){
            logService.addLog("delete schedule object [" + scheduleObject.getName() +"] ");
            LogGameDesignAction.getInstance().info("deleteScheduleObject|{}|{}|{}|{}|{}" , username, game, country, scheduleObject.getName(), id);
        }else{
            LogGameDesignAction.getInstance().info("deleteScheduleObject|{}|{}|{}|{}" , username, game, country, id);
        }
        scheduleObjectRepository.deleteById(id);
    }

    public boolean scanRunOffer(){
        LogGameDesignAction.getInstance().info("scanRunOffer 1. staring ...");
        RunOffer runOffer = runOfferRepository.findFirstByStatus(Config.WAIT_SCAN).orElse(null);
        if(runOffer == null){
            runOffer = runOfferRepository.findFirstByStatus(Config.WAIT_RESCAN).orElse(null);
        }
        if(runOffer == null){
            LogGameDesignAction.getInstance().info("scanRunOffer 2. end not found ...");
            return false;
        }
        scanRunOffer(runOffer);
        return true;
    }

    private void scanRunOffer(RunOffer runOffer) {
        if(runOffer == null){
            LogSystemAction.getInstance().info("scanRunOffer stopped. end not found ...");
            return;
        }
        String idRunOffer = runOffer.getIdRunOffer();
        String game = runOffer.getGame();
        String country = runOffer.getCountry();
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
        LogSystemAction.getInstance().info("scanRunOffer 2. running|{}|{}|{}", idRunOffer, game, country);
        Object object = runOffer.getObject();
        if(object != null){
            runOffer.setCountTotal(object.getTotalUser());
            runOffer.setStatus(Config.SCANNED);
            runOfferRepository.save(runOffer);
            addNextRunOffer(runOffer);
        }else{
            LogSystemAction.getInstance().error("scanRunOffer 3. end object null ...|{}|{}|{}", idRunOffer, game, country);
            runOfferRepository.delete(runOffer);
        }
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }

    private void addNextRunOffer(RunOffer oldRunOffer) {
        Date current = new Date();
        if(oldRunOffer == null || oldRunOffer.getObject() == null || oldRunOffer.getOffer() == null ||
                oldRunOffer.getScheduleFrequency() <= 0 || current.getTime() >= oldRunOffer.getTimeEndSchedule().getTime()){
            return;
        }

        Date newTimeStart = new Date(oldRunOffer.getTimeStart().getTime() + ((long) oldRunOffer.getScheduleFrequency())*DateTimeConstant.MILLIS_PER_HOUR);
        Date newTimeEnd = new Date(oldRunOffer.getTimeEnd().getTime() + ((long) oldRunOffer.getScheduleFrequency())*DateTimeConstant.MILLIS_PER_HOUR);
        RunOffer newRunOffer = new RunOffer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(newTimeStart);
        newRunOffer.setIdRunOffer(oldRunOffer.getObject().getIdObject()+"_"+oldRunOffer.getOffer().getIdOffer()+"_"+strDate);
        newRunOffer.setObject(oldRunOffer.getObject());
        newRunOffer.setOffer(oldRunOffer.getOffer());
        newRunOffer.setPriority(oldRunOffer.getPriority());
        newRunOffer.setTimeStart(newTimeStart);
        newRunOffer.setTimeEnd(newTimeEnd);
        newRunOffer.setNote(oldRunOffer.getNote());
        newRunOffer.setStatus(Config.WAIT_SCAN);
        newRunOffer.setGame(oldRunOffer.getGame());
        newRunOffer.setCountry(oldRunOffer.getCountry());
        newRunOffer.setScheduleFrequency(oldRunOffer.getScheduleFrequency());
        newRunOffer.setTimeEndSchedule(oldRunOffer.getTimeEndSchedule());
        newRunOffer.setPreTimeSetSchedule(oldRunOffer.getPreTimeSetSchedule());
        RunOffer save = runOfferRepository.save(newRunOffer);
        try {
            addScanScheduleRunOffer(save);
        } catch (Exception e){

        }
    }

    public void addScanScheduleRunOffer(RunOffer save){
        Object object = save.getObject();
        String idObject = "#";
        if(object!=null){
            idObject = object.getIdObject();
        }
        LogSystemAction.getInstance().info("addScanRunOffer|" + save.getIdRunOffer() +"|" + idObject);

        RunOfferJob job = new RunOfferJob(save.getTimeStart().getTime() - save.getPreTimeSetSchedule()* DateTimeConstant.MILLIS_PER_HOUR);
        job.setRunOffer(save);
        job.setIdRunOffer(save.getId());
        try {
            CronJobServiceImpl.getInstance().addJob(job, false);
        }catch (Exception e){
            LogSystemAction.getInstance().error("addScanRunOffer exception|" + save.getIdRunOffer() +"|" + idObject);
            e.printStackTrace();
        }
    }

    public String scanObject(Date curTime){
        LogSystemAction.getInstance().info("scanObject 1. staring ...");
        ScheduleObject scheduleObject = scheduleObjectRepository.findFirstByTimeScanLessThanEqualAndStatus(curTime, Config.WAIT_SCAN).orElse(null);
        if(scheduleObject == null){
            scheduleObject = scheduleObjectRepository.findFirstByTimeScanLessThanEqualAndStatus(curTime, Config.WAIT_RESCAN).orElse(null);
        }
        if(scheduleObject == null){
            LogSystemAction.getInstance().info("scanObject 2. end not found ...");
            return null;
        }
        scanObject(scheduleObject , new AtomicLong());
        return scheduleObject.getObject().getId();

    }

    private void scanObject(ScheduleObject scheduleObject , AtomicLong processRecord) {
        String name = scheduleObject.getName();
        String game = scheduleObject.getGame();
        String country = scheduleObject.getCountry();
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
        LogSystemAction.getInstance().info("scanObject 2. running|{}|{}|{}", name, game, country);
        Object object = scheduleObject.getObject();
        if(object != null){
            String idObject = object.getIdObject();
            //LogLoggic.getInstance().info("scanObject 3. running|{}|{}|{}|{}", name, game, country, idObject);
            int status = object.getStatus();
            object.setStatus(Config.SCANNING);
            objectRepository.save(object);
            scheduleObject.setStatus(Config.SCANNING);
            scheduleObjectRepository.save(scheduleObject);

            //1.remove userObject had object
            //3.add All userObject

            //1.
            //2.
            if(status == Config.WAIT_RESCAN || status == Config.SCANNED){
                LogSystemAction.getInstance().info("scanObject 3.1 delete object user old|{}|{}|{}|{}", name, game, country, idObject);
                userObjectRepository.deleteAllByObject(object);
            }


            Stream<User> allByGameAndCountry = userRepository.findAll().stream();
            ConditionGame condition = ConditionController.getInstance().getCondition(game);
            allByGameAndCountry.filter(user -> helpers.isEnoughCondition(user, object,condition)).forEach(user -> {
                processRecord.getAndIncrement();
                UserObject userObject = new UserObject();
                userObject.setObject(object);
                userObject.setUser(user);
                userObjectRepository.save(userObject);
                object.setTotalUser(processRecord.get());
                objectRepository.save(object);
            });

            //LogLoggic.getInstance().info("scanObject 5. re-run runOffer|{}|{}|{}|{}", name, game, country, idObject);
            Stream<RunOffer> runOfferStream = runOfferRepository.streamAllByObjectAndTimeEndAfter(object, scheduleObject.getTimeScan());
            runOfferStream.forEach(runOffer -> {
                runOffer.setStatus(Config.WAIT_RESCAN);
                RunOffer save = runOfferRepository.save(runOffer);
                try {
                    addScanRunOffer(save);
                } catch (CronJobException e) {
                    e.printStackTrace();
                }
            });

            object.setTotalUser(processRecord.get());
            object.setStatus(Config.SCANNED);
            objectRepository.save(object);


            customDataService.updateTotalUserCustomGift(object.getId(), object.getTotalUser());

            LogSystemAction.getInstance().info("scanObject 6. done|{}|{}|{}|{}|{}", name, game, country, idObject,processRecord.get());
            scheduleObject.setStatus(Config.SCANNED);
            scheduleObjectRepository.save(scheduleObject);
        }else{
            LogSystemAction.getInstance().error("scanObject 3. ended object null|{}|{}|{}", name, game, country);
            scheduleObjectRepository.delete(scheduleObject);
        }
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }

    public Date getTimeScanNextIfHad(Date curTime) {
        ScheduleObject scheduleObject = scheduleObjectRepository.findFirstByTimeScanLessThanEqualAndStatus(curTime, Config.WAIT_SCAN).orElse(null);
        if(scheduleObject == null){
            scheduleObject = scheduleObjectRepository.findFirstByTimeScanLessThanEqualAndStatus(curTime, Config.WAIT_RESCAN).orElse(null);
        }
        if(scheduleObject != null){
            return scheduleObject.getTimeScan();
        }
        return null;
    }

    public boolean needScanRunOffer() {
        List<Integer> list = Arrays.asList(Config.WAIT_SCAN,Config.WAIT_SCAN);
        return runOfferRepository.existsByStatusIn(list);
    }

    public void scanRunOffer(String idRunOffer, RunOffer runOffer1) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(runOffer1.getGame(), runOffer1.getCountry());
        LogSystemAction.getInstance().info("scanRunOffer 1. staring ..." + idRunOffer);
        RunOffer runOffer = runOfferRepository.findById(idRunOffer).orElse(null);
        if(runOffer == null){
            LogErrorAction.getInstance().error("scanRunOffer|runOffer not found|" + idRunOffer + "|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());

        }
        scanRunOffer(runOffer);
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }

    public void scanObject(String idScheduleObject, ScheduleObject object, AtomicLong processRecord) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(object.getGame(),object.getCountry());
        LogSystemAction.getInstance().info("scanObject 1. staring ..." + idScheduleObject);
        ScheduleObject scheduleObject = scheduleObjectRepository.findById(idScheduleObject).orElse(null);
        if(scheduleObject == null){
            LogSystemAction.getInstance().info("scanObject 2. end not found ..." + idScheduleObject);
            LogErrorAction.getInstance().error("scanObject|scheduleObject not found|" + idScheduleObject + "|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
            return;
        }
        if(scheduleObject.getStatus() == Config.SCANNED){
            LogSystemAction.getInstance().info("scanObject 2. end was scan ..." + idScheduleObject);
            return;
        }
        scanObject(scheduleObject, processRecord);
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }
    private void addScanRunOffer(com.zingplay.models.RunOffer save) throws CronJobException {
        //if(save.getObject().getStatus() == Config.SCANNED)
        {
            Object object = save.getObject();
            String idObject = "#";
            if(object!=null){
                idObject = object.getIdObject();
            }
            LogSystemAction.getInstance().info("addScanRunOffer|" + save.getIdRunOffer() +"|" + idObject);

            RunOfferJob job = new RunOfferJob(System.currentTimeMillis());
            job.setRunOffer(save);
            job.setIdRunOffer(save.getId());
            try {
                CronJobServiceImpl.getInstance().addJob(job, false);
            }catch (Exception e){
                LogSystemAction.getInstance().error("addScanRunOffer exception|" + save.getIdRunOffer() +"|" + idObject);
                e.printStackTrace();
            }        }
    }

    public void scanRunOfferToObject(String idRunOffer, String idObject, RunOffer runOffer1, AtomicLong processRecord) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(runOffer1.getGame(),runOffer1.getCountry());
        LogSystemAction.getInstance().info("scanRunOfferToObject|" + idRunOffer +"|" + idObject);
        RunOffer runOffer = runOfferRepository.findById(idRunOffer).orElse(null);
        Object object = objectRepository.findById(idObject).orElse(null);
        if(runOffer == null){
            LogSystemAction.getInstance().info("scanRunOfferToObject|failed runOffer null|" + idRunOffer +"|" + idObject);
            LogErrorAction.getInstance().error("scanRunOfferToObject|runOffer not found|" + idRunOffer  +"|" + idObject +  "|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
            return;
        }
        if(object == null){
            LogSystemAction.getInstance().info("scanRunOfferToObject|failed object null|" + idRunOffer +"|" + idObject);
            return;
        }

        //3.
        long totalUser = object.getTotalUser();
        Stream<UserBuyOffer> userBuyOfferStream = userBuyOfferRepository.streamAllByRunOffer(runOffer);
        userBuyOfferStream.forEach(userBuyOffer -> {
            User user = userBuyOffer.getUser();

            userObjectRepository.findFirstByUserAndObject(user, object).orElseGet(() -> {
                processRecord.getAndIncrement();
                UserObject userobject = new UserObject();
                userobject.setObject(object);
                userobject.setUser(user);
                userObjectRepository.save(userobject);
                return  userobject;
            });
        });
        object.setTotalUser(totalUser + processRecord.get());
        objectRepository.save(object);
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
        LogSystemAction.getInstance().info("scanRunOfferToObject|done|" + idRunOffer +"|" + idObject +"|" + processRecord.get());
    }
}
