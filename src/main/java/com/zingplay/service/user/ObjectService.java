package com.zingplay.service.user;

import com.zingplay.beans.Object;
import com.zingplay.beans.ObjectCustomCondition;
import com.zingplay.beans.TimeSchedule;
import com.zingplay.cron.CronJobServiceImpl;
import com.zingplay.cron.beans.job.ScanUserGroupJob;
import com.zingplay.cron.exception.CronJobException;
import com.zingplay.enums.Config;
import com.zingplay.helpers.GsonHelper;
import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.log.LogSystemAction;
import com.zingplay.models.ConditionObject;
import com.zingplay.models.ScheduleObject;
import com.zingplay.module.objects.ConditionController;
import com.zingplay.module.objects.ConditionObjectRepository;
import com.zingplay.module.objects.ConditionRepository;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.ObjectRepository;
import com.zingplay.repository.ScheduleObjectRepository;
import com.zingplay.repository.UserObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ObjectService {

    private final UserObjectRepository userObjectRepository;
    private final ObjectRepository objectRepository;
    private final ConditionRepository conditionRepository;
    private final ConditionObjectRepository conditionObjectRepository;
    private final ScheduleObjectRepository scheduleObjectRepository;
    private final LogService logService;
    private final Helpers helpers;
    @Autowired
    public ObjectService(UserObjectRepository userObjectRepository, ObjectRepository objectRepository, ConditionRepository conditionRepository, ConditionObjectRepository conditionObjectRepository, ScheduleObjectRepository scheduleObjectRepository, LogService logService, Helpers helpers) {
        this.userObjectRepository = userObjectRepository;
        this.objectRepository = objectRepository;
        this.conditionRepository = conditionRepository;
        this.conditionObjectRepository = conditionObjectRepository;
        this.scheduleObjectRepository = scheduleObjectRepository;
        this.logService = logService;
        this.helpers = helpers;
    }

    public Page<?> getAllObject(String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(search!=null && !search.isEmpty()){
            Page<com.zingplay.models.Object> list = objectRepository.findByIdObjectContaining(search, pageable);
            if(list.getContent().isEmpty()){
                list = objectRepository.findById(search, pageable);
            }
            return list;
        }
        return objectRepository.findAll(pageable);
    }

    public int createAll(List<Object> objects) {
        //tao list object -> override = 1 -> update;
        for (Object object : objects) {
            object.autoTrim();
        }
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        LogGameDesignAction.getInstance().info("createAllObjects|{}|{}|{}|{}" , username, game, country, objects.size());
        int count = 0;
        for (Object object : objects) {
            com.zingplay.models.Object objectFound = objectRepository.findFirstByIdObject(object.getIdObject()).orElse(null);
            if(objectFound!=null && !object.isOverride()){
                LogGameDesignAction.getInstance().info("createAllObjects not override|{}|{}|{}|{}" , username, game, country, objectFound.getIdObject());
                continue;
            }
            if(objectFound == null){
                objectFound = new com.zingplay.models.Object();
                objectFound.setIdObject(object.getIdObject());
                objectFound.setStatus(Config.WAIT_SCAN);
                objectFound.setGame(game);
                objectFound.setCountry(country);
            }else{
                objectFound.setStatus(Config.WAIT_RESCAN);
            }

            LogGameDesignAction.getInstance().info("createAllObjects imported|{}|{}|{}|{}" , username, game, country, objectFound.getIdObject());
            objectFound.setNameObject(object.getNameObject());
            objectFound.setNote(object.getNote());
            objectFound.setChannelPayments(object.getChannelPayments());
            objectFound.setLastPaidAmounts(object.getLastPaidAmounts());
            objectFound.setPaidTotalMin(object.getPaidTotalMin());
            objectFound.setPaidTotalMax(object.getPaidTotalMax());
            objectFound.setPaidTimesMin(object.getPaidTimesMin());
            objectFound.setPaidTimesMax(object.getPaidTimesMax());
            objectFound.setTotalGameMin(object.getTotalGameMin());
            objectFound.setTotalGameMax(object.getTotalGameMax());
            objectFound.setAgeUserMin(object.getAgeUserMin());
            objectFound.setAgeUserMax(object.getAgeUserMax());
            objectFound.setTimeOnlineMin(object.getTimeOnlineMin());
            objectFound.setTimeOnlineMax(object.getTimeOnlineMax());
            objectFound.setChannelMin(object.getChannelMin());
            objectFound.setChannelMax(object.getChannelMax());
            objectFound.setScheduleFrequency(0);
            com.zingplay.models.Object save = objectRepository.save(objectFound);

            Set<TimeSchedule> times = object.getTimes();
            if(times == null){
                times = new HashSet<>();
            }

            Set<Long> timeStart = object.getTimeStart();
            if(timeStart != null){
                for (Long aLong : timeStart) {
                    TimeSchedule timeSchedule = new TimeSchedule();
                    timeSchedule.setName( objectFound.getIdObject() + "_" + aLong);
                    timeSchedule.setTimeScan(new Date(aLong * 1000));
                    times.add(timeSchedule);
                }
            }

            if(object.isScanNow()){
                //add vao scan ne
                TimeSchedule timeSchedule = new TimeSchedule();
                timeSchedule.setName("now");
                timeSchedule.setTimeScan(new Date());
                times.add(timeSchedule);
            }
            this.addObjectToScan(save,times);
            count ++;
        }
        logService.addLog("Import object from file [" + count +"] object");
        return  count;
    }

    public ResponseEntity<?> create(Object objectCreate) {
        objectCreate.autoTrim();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        objectCreate.setGame(game);
        objectCreate.setCountry(country);
        LogGameDesignAction.getInstance().info("create Object creating|{}|{}|{}|{}" , helpers.getUsername(), game, country, objectCreate.toString());
        boolean b = objectRepository.existsObjectByIdObject(objectCreate.getIdObject());
        if(b){
            LogGameDesignAction.getInstance().error("create Object fail idObject had exists|{}|{}|{}|{}" , helpers.getUsername(), game, country, objectCreate.getIdObject());
            return ResponseEntity.badRequest().body(new MessageResponse("Object Id had exists!!"));
        }else{
            com.zingplay.models.Object object = toObjectModel(objectCreate);
            object.setStatus(Config.WAIT_SCAN);
            com.zingplay.models.Object save = objectRepository.save(object);

            LogGameDesignAction.getInstance().info("create Object created|{}|{}|{}|{}" , helpers.getUsername(), game, country, objectCreate.getIdObject());
            logService.addLog("create object [" + object.getIdObject() +"]");

            Set<TimeSchedule> times = objectCreate.getTimes();
            if(times == null){
                times = new HashSet<>();
            }
            if(objectCreate.isScanNow()){
                //add vao scan ne
                TimeSchedule timeSchedule = new TimeSchedule();
                timeSchedule.setName("immediate");
                timeSchedule.setTimeScan(new Date());
                times.add(timeSchedule);
            }
            this.addObjectToScan(save,times);

            return ResponseEntity.ok().body(save);
        }
    }


    public com.zingplay.models.Object updateObject(String id, Object object){
        object.autoTrim();
        LogGameDesignAction.getInstance().info("updateObject updating|{}|{}|{}" , helpers.getUsername(), id, object.toString());
        return objectRepository.findById(id).map(objUpdate -> {
            LogGameDesignAction.getInstance().info("updateObject updated|{}|{}|{}" , helpers.getUsername(), id, object.getIdObject());
            objUpdate.setIdObject(object.getIdObject());
            objUpdate.setGame(object.getGame());
            objUpdate.setCountry(object.getCountry());
            objUpdate.setNameObject(object.getNameObject());
            objUpdate.setNote(object.getNote());
            objUpdate.setChannelPayments(object.getChannelPayments());
            objUpdate.setLastPaidAmounts(object.getLastPaidAmounts());
            objUpdate.setPaidTotalMin(object.getPaidTotalMin());
            objUpdate.setPaidTotalMax(object.getPaidTotalMax());
            objUpdate.setPaidTimesMin(object.getPaidTimesMin());
            objUpdate.setPaidTimesMax(object.getPaidTimesMax());
            objUpdate.setTotalGameMin(object.getTotalGameMin());
            objUpdate.setTotalGameMax(object.getTotalGameMax());
            objUpdate.setAgeUserMin(object.getAgeUserMin());
            objUpdate.setAgeUserMax(object.getAgeUserMax());
            objUpdate.setTimeOnlineMin(object.getTimeOnlineMin());
            objUpdate.setTimeOnlineMax(object.getTimeOnlineMax());
            objUpdate.setChannelMin(object.getChannelMin());
            objUpdate.setChannelMax(object.getChannelMax());
            objUpdate.setStatus(Config.WAIT_RESCAN);
            com.zingplay.models.Object save = objectRepository.save(objUpdate);

            Set<TimeSchedule> times = object.getTimes();
            if(times == null){ times = new HashSet<>(); }
            if(object.isScanNow()){
                //add vao scan ne
                TimeSchedule timeSchedule = new TimeSchedule();
                timeSchedule.setName("edit-re-scan");
                timeSchedule.setTimeScan(new Date());
                times.add(timeSchedule);
            }
            this.addObjectToScan(save,times);
            //addObjectToScanImmediate(save, new Date());
            logService.addLog("update object [" + object.getIdObject() +"]");
            return save;
        }).orElse(null);
    }

    public ResponseEntity<?> deleteObject(String id){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        com.zingplay.models.Object object = objectRepository.findById(id).orElse(null);
        if(object != null){
            ConditionObject condition = object.getCondition();
            if(condition != null){
                conditionObjectRepository.delete(condition);
            }
            userObjectRepository.deleteAllByObject(object);
            logService.addLog("delete object [" + object.getIdObject() +"]");
            LogGameDesignAction.getInstance().info("deleteObject deleted|{}|{}|{}|{}|{}" , username, game, country,object.getIdObject(), id);
        }else{
            LogGameDesignAction.getInstance().info("deleteObject deleted|{}|{}|{}|{}" , username, game, country, id);
        }
        scheduleObjectRepository.deleteAllByObject(object);
        objectRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("success"));
    }

    private Object find(List<Object> list, String idObject){
        return list.stream().filter(object -> idObject.equals(object.getIdObject())).findFirst().orElse(null);
    }
    private List<com.zingplay.models.Object> toObjects(List<Object> list){
        return list.stream().map(this::toObjectModel).collect(Collectors.toList());
    }
    private com.zingplay.models.Object toObjectModel(Object object){
        com.zingplay.models.Object objUpdate = new com.zingplay.models.Object();
        objUpdate.setIdObject(object.getIdObject());
        objUpdate.setGame(object.getGame());
        objUpdate.setCountry(object.getCountry());
        objUpdate.setNameObject(object.getNameObject());
        objUpdate.setNote(object.getNote());
        objUpdate.setChannelPayments(object.getChannelPayments());
        objUpdate.setLastPaidAmounts(object.getLastPaidAmounts());
        objUpdate.setPaidTotalMin(object.getPaidTotalMin());
        objUpdate.setPaidTotalMax(object.getPaidTotalMax());
        objUpdate.setPaidTimesMin(object.getPaidTimesMin());
        objUpdate.setPaidTimesMax(object.getPaidTimesMax());
        objUpdate.setTotalGameMin(object.getTotalGameMin());
        objUpdate.setTotalGameMax(object.getTotalGameMax());
        objUpdate.setAgeUserMin(object.getAgeUserMin());
        objUpdate.setAgeUserMax(object.getAgeUserMax());
        objUpdate.setTimeOnlineMin(object.getTimeOnlineMin());
        objUpdate.setTimeOnlineMax(object.getTimeOnlineMax());
        objUpdate.setChannelMin(object.getChannelMin());
        objUpdate.setChannelMax(object.getChannelMax());
        objUpdate.setScheduleFrequency(0);
        return objUpdate;
    }
    private boolean containsIdObjects(final List<com.zingplay.models.Object> objects, final String idObject){
        //return users.stream().map(User::getIdUser).filter(userId::equals).findFirst().isPresent();
        return objects.stream().map(com.zingplay.models.Object::getIdObject).anyMatch(idObject::equals);
    }
    private List<String> getObjectIds(List<Object> objects){
        List<String> results = new ArrayList<>();
        for (Object user : objects) {
            results.add(user.getIdObject());
        }
        return results;
    }

    private ScheduleObject addObjectToScanImmediate(com.zingplay.models.Object object, Date time){
        ScheduleObject schedule = new ScheduleObject();
        schedule.setObject(object);
        schedule.setName("immediate");
        schedule.setTimeScan(time);
        schedule.setStatus(Config.WAIT_SCAN);
        schedule.setGame(object.getGame());
        schedule.setCountry(object.getCountry());

        ScheduleObject save = scheduleObjectRepository.save(schedule);
        LogGameDesignAction.getInstance().info("addObjectToScanImmediate|{}|{}|{}", object.getIdObject(), schedule.getName(), schedule.getTimeScan());
        //cron.startScanObject(time);
        addCronScanObject(save);
        return save;
    }
    private List<ScheduleObject> addObjectToScanImmediate(com.zingplay.models.Object object, Set<Date> times){
        ArrayList<ScheduleObject> scheduleObjects = new ArrayList<>();
        for (Date date : times) {
            ScheduleObject schedule = new ScheduleObject();
            schedule.setObject(object);
            schedule.setName("immediate");
            schedule.setTimeScan(date);
            schedule.setStatus(Config.WAIT_SCAN);
            schedule.setGame(object.getGame());
            schedule.setCountry(object.getCountry());
            scheduleObjects.add(schedule);

        }
        for (int i = 0; i < scheduleObjects.size(); i++) {
            scheduleObjects.get(i).setName("s" + i);
        }
        List<ScheduleObject> saves = scheduleObjectRepository.saveAll(scheduleObjects);
        //cron.startScanObject(times);
        addCronScanObjects(saves);
        return saves;
    }

    private void addCronScanObjects(List<ScheduleObject> saves) {
        for (ScheduleObject save : saves) {
            addCronScanObject(save);
        }
    }

    private void addCronScanObject(ScheduleObject save) {
        com.zingplay.models.Object object = save.getObject();
        long time = save.getTimeScan().getTime();
        String objectId  = "#";
        if(object!= null){
            objectId = object.getIdObject();
        }
        LogSystemAction.getInstance().info("addCronScanObject|" + objectId + "|" + save.getTimeScan());

        ScanUserGroupJob job = new ScanUserGroupJob(time);
        job.setScheduleObject(save);
        job.setIdScheduleObject(save.getId());
        try {
            CronJobServiceImpl.getInstance().addJob(job, false);
        } catch (CronJobException e) {
            LogSystemAction.getInstance().error("addCronScanObject exception|" + objectId + "|" + save.getTimeScan());
            e.printStackTrace();
        }
    }

    public void addObjectToScan(com.zingplay.models.Object object, Set<TimeSchedule> times) {
        if(times.size() <= 0) return;
        ArrayList<ScheduleObject> scheduleObjects = new ArrayList<>();
        times.forEach(timeSchedule -> {
            ScheduleObject schedule = new ScheduleObject();
            schedule.setObject(object);
            schedule.setName(timeSchedule.getName());
            schedule.setTimeScan(timeSchedule.getTimeScan());
            schedule.setStatus(Config.WAIT_SCAN);
            schedule.setGame(object.getGame());
            schedule.setCountry(object.getCountry());
            scheduleObjects.add(schedule);
            LogGameDesignAction.getInstance().info("addObjectToScan|{}|{}|{}", object.getIdObject(), timeSchedule.getName(), timeSchedule.getTimeScan());
        });
        List<ScheduleObject> saves = scheduleObjectRepository.saveAll(scheduleObjects);
        addCronScanObjects(saves);
        //cron.startScanObjectVia(times);
    }

    public ResponseEntity<?> createCustomCondition(ObjectCustomCondition request) {
        //1. check exsit
        //2. check valid condition
        //3. convert codition
        //4. save

        String username = helpers.getUsername();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        LogGameDesignAction.getInstance().info("object custom condition creating|{}|{}|{}|{}" , username, game, country, request.toString());
        request.autoTrim();

        //1.
        String idObject = request.getIdObject();
        boolean b = objectRepository.existsObjectByIdObject(idObject);
        if(b){
            LogGameDesignAction.getInstance().error("create Object custom condition fail idObject had exists|{}|{}|{}|{}" , username, game, country, idObject);
            return ResponseEntity.badRequest().body(new MessageResponse("Object Id had exists!!"));
        }else{
            //2.
            ConditionObject condition = request.getCondition();
            boolean validAllKey = ConditionController.getInstance().isValidAllKey(game,condition);
            if(!validAllKey){
                return ResponseEntity.badRequest().body(new MessageResponse("Key not exist, please config first!"));
            }
            condition = conditionObjectRepository.save(condition);

            //4.
            com.zingplay.models.Object object = new com.zingplay.models.Object();
            object.setCondition(condition);
            object.setIdObject(idObject);
            object.setStatus(Config.WAIT_SCAN);
            object.setTotalUser(0);
            object.setGame(game);
            object.setCountry(country);
            object.setScheduleFrequency(request.getScheduleFrequency());
            com.zingplay.models.Object save = objectRepository.save(object);

            LogGameDesignAction.getInstance().info("created object custom condition|{}|{}|{}|{}" , username, game, country, GsonHelper.toJson(save));
            logService.addLog("created object custom condition [" + idObject +"]");

            Set<TimeSchedule> times = request.getTimes();
            if(times == null){ times = new HashSet<>(); }
            this.addObjectToScan(save,times);

            return ResponseEntity.ok().body(save);
        }
    }
    public ResponseEntity<?> createCustomConditions(List<ObjectCustomCondition> request) {
        //1. check exsit
        //2. check valid condition
        //3. convert codition
        //4. save

        String username = helpers.getUsername();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        for (int i = 0; i < request.size(); i++) {
            ObjectCustomCondition objectCustomCondition = request.get(i);
            LogGameDesignAction.getInstance().info("object custom condition creating|{}|{}|{}|{}" , username, game, i, objectCustomCondition.toString());
            objectCustomCondition.autoTrim();
        }

        //1.
        List<String> idObjects = request.stream().map(ObjectCustomCondition::getIdObject).collect(Collectors.toList());
        boolean b = objectRepository.existsObjectByIdObjectIn(idObjects);
        if(b){
            List<com.zingplay.models.Object> objects = objectRepository.findByIdObjectIn(idObjects);
            StringBuilder idObjectStrs = new StringBuilder();
            for (com.zingplay.models.Object object : objects) {
                idObjectStrs.append((idObjectStrs.length() == 0) ? "" : ", ").append(object.getIdObject());
            }
            LogGameDesignAction.getInstance().error("create object custom condition fail idObject had exists|{}|{}|{}|{}" , username, game, country, idObjectStrs);
            return ResponseEntity.badRequest().body(new MessageResponse("Object Id had exists!!|" + idObjectStrs));
        }else{
            //2.

            for (ObjectCustomCondition objectCustomCondition : request) {
                ConditionObject condition = objectCustomCondition.getCondition();
                boolean validAllKey = ConditionController.getInstance().isValidAllKey(game,condition);
                if(!validAllKey){
                    LogGameDesignAction.getInstance().error("create object custom condition fail KeyCondition not exist|{}|{}|{}|{}" , username, game, country, objectCustomCondition.getIdObject());
                    return ResponseEntity.badRequest().body(new MessageResponse(objectCustomCondition.getIdObject() +" | KeyCondition not exist, please config first!"));
                }
            }

            //4.
            ArrayList<com.zingplay.models.Object> saved = new ArrayList<>();
            for (ObjectCustomCondition objectCustomCondition : request) {
                ConditionObject condition = objectCustomCondition.getCondition();
                condition = conditionObjectRepository.save(condition);
                String idObject = objectCustomCondition.getIdObject();
                com.zingplay.models.Object object = new com.zingplay.models.Object();
                object.setCondition(condition);
                object.setIdObject(idObject);
                object.setStatus(Config.WAIT_SCAN);
                object.setTotalUser(0);
                object.setScheduleFrequency(objectCustomCondition.getScheduleFrequency());
                com.zingplay.models.Object save = objectRepository.save(object);
                LogGameDesignAction.getInstance().info("created object custom condition|{}|{}|{}|{}" , username, game, country, GsonHelper.toJson(save));
                logService.addLog("created object custom condition [" + idObject +"]");
                Set<TimeSchedule> times = objectCustomCondition.getTimes();
                if(times == null){ times = new HashSet<>(); }
                this.addObjectToScan(save,times);
                saved.add(save);
            }
            return ResponseEntity.ok().body(saved);
        }
    }
    public ResponseEntity<?> updateCustomCondition(String id,ObjectCustomCondition request) {
        //1. check exist
        //2. check valid condition
        //3. convert codition
        //4. save

        String username = helpers.getUsername();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        LogGameDesignAction.getInstance().info("object custom condition updating|{}|{}|{}|{}" , username, game, country, request.toString());
        request.autoTrim();

        //1.
        com.zingplay.models.Object object = objectRepository.findById(id).orElse(null);
        if(object == null){
            LogGameDesignAction.getInstance().error("update Object fail id not found.|" + id);
            return ResponseEntity.badRequest().body(new MessageResponse("Object Id had exists!!"));
        }
        ConditionObject condition = object.getCondition();
        object.setIdObject(request.getIdObject());
        object.setScheduleFrequency(request.getScheduleFrequency());
        ConditionObject conditionObject = request.getCondition();
        if(condition == null){
            condition = conditionObjectRepository.save(conditionObject);
            object.setCondition(condition);
        } else {
            //2.
            boolean validAllKey = ConditionController.getInstance().isValidAllKey(game,condition);
            if(!validAllKey){
                return ResponseEntity.badRequest().body(new MessageResponse("Key not exist, please config first!"));
            }
            //4.
            condition.setInListFloat(conditionObject.getInListFloat());
            condition.setInListLong(conditionObject.getInListLong());
            condition.setInListStr(conditionObject.getInListStr());
            condition.setInListObject(conditionObject.getInListObject());
            condition.setInRangeLong(conditionObject.getInRangeLong());
            condition.setInRangeFloat(conditionObject.getInRangeFloat());
            condition.setInRangeDuration(conditionObject.getInRangeDuration());
            condition.setInListDuration(conditionObject.getInListDuration());
            conditionObjectRepository.save(condition);
        }
        object.setStatus(object.getStatus() != Config.WAIT_SCAN?Config.WAIT_RESCAN:Config.WAIT_SCAN);

        com.zingplay.models.Object saved = objectRepository.save(object);
        Set<TimeSchedule> times = request.getTimes();
        if(times == null){ times = new HashSet<>();}
        this.addObjectToScan(saved,times);

        LogGameDesignAction.getInstance().info("updated object custom condition|{}|{}|{}|{}" , username, game, country, GsonHelper.toJson(saved));
        logService.addLog("updated object custom condition [" + saved.getIdObject() +"]");

        return ResponseEntity.ok().body(saved);
    }
}
