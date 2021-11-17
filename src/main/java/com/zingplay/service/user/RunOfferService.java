package com.zingplay.service.user;

import com.zingplay.beans.RunOffer;
import com.zingplay.cron.CronJobServiceImpl;
import com.zingplay.cron.beans.job.RunOfferAddToObjectJob;
import com.zingplay.cron.beans.job.RunOfferJob;
import com.zingplay.cron.exception.CronJobException;
import com.zingplay.enums.Config;
import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.log.LogSystemAction;
import com.zingplay.models.Object;
import com.zingplay.models.Offer;
import com.zingplay.models.User;
import com.zingplay.module.telegram.TelegramConst;
import com.zingplay.module.telegram.TelegramController;
import com.zingplay.payload.request.ObjectFromRunOfferCreate;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RunOfferService {

    private final UserRepository userRepository;
    private final RunOfferRepository runOfferRepository;
    private final UserBuyOfferRepository userBuyOfferRepository;
    private final OfferRepository offerRepository;
    private final ObjectRepository objectRepository;
    private final Helpers helpers;
    private final LogService logService;

    @Autowired
    public RunOfferService(UserRepository userRepository, RunOfferRepository runOfferRepository, @Lazy UserBuyOfferRepository userBuyOfferRepository, OfferRepository offerRepository, ObjectRepository objectRepository, Helpers helpers, LogService logService) {
        this.userRepository = userRepository;
        this.runOfferRepository = runOfferRepository;
        this.userBuyOfferRepository = userBuyOfferRepository;
        this.offerRepository = offerRepository;
        this.objectRepository = objectRepository;
        this.helpers = helpers;
        this.logService = logService;
    }

    public Page<?> getAllRunOffer(String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();

        PageRequest of = PageRequest.of(0, 20);
        if(search!=null && !search.isEmpty()){
            Page<com.zingplay.models.RunOffer> byIdRunOfferContainingAndGameAndCountry = runOfferRepository.findByIdRunOfferContainingAndGameAndCountry(search, game, country, pageable);
            List<com.zingplay.models.RunOffer> runOffers = byIdRunOfferContainingAndGameAndCountry.getContent();
            if(runOffers.isEmpty()){
                Page<Object> byIdObjectContainingAndGameAndCountry = objectRepository.findByIdObjectContaining(search, of);
                List<Object> objects = byIdObjectContainingAndGameAndCountry.getContent();
                if(!objects.isEmpty()){
                    byIdRunOfferContainingAndGameAndCountry = runOfferRepository.findByObjectIn(objects, pageable);
                }else{
                    Page<Offer> byIdOfferContainingAndGameAndCountry = offerRepository.findByIdOfferContainingAndGameAndCountry(search, game, country, of);
                    List<Offer> offers = byIdOfferContainingAndGameAndCountry.getContent();
                    if(!offers.isEmpty()){
                        byIdRunOfferContainingAndGameAndCountry = runOfferRepository.findByOfferIn(offers, pageable);
                    }else{
                        byIdRunOfferContainingAndGameAndCountry = runOfferRepository.findByIdAndGameAndCountry(search, game, country, pageable);
                    }
                }
            }else{
                byIdRunOfferContainingAndGameAndCountry = runOfferRepository.findByIdAndGameAndCountry(search, game, country, pageable);
            }
            return byIdRunOfferContainingAndGameAndCountry;
        }
        return runOfferRepository.findAllByGameAndCountry(game, country, pageable);
    }

    public Page<?> getAllRunOfferInDay(Long from, Long to, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();

        Date fromDate = new Date(from);
        Date toDate = new Date(to);
        return runOfferRepository.findAllByGameAndCountryAndTimeStartBeforeAndTimeEndAfter(game, country,toDate,fromDate, pageable);
    }

    public boolean isExistRunOffer(String idRunOffer){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        return runOfferRepository.existsByIdRunOfferAndGameAndCountry(idRunOffer,game,country);

    }
    public Page<?> getUserBuyRunOffer(String id, String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();

        if(search!=null && !search.isEmpty()){
            Integer userId = Integer.parseInt(search);
            Page<User> byUserIdContainingAndGameAndCountry = userRepository.findByUserId(userId, pageable);
            List<User> content = byUserIdContainingAndGameAndCountry.getContent();
            return userBuyOfferRepository.findByRunOffer_IdAndUserIn(id, content, pageable);
        }
        return userBuyOfferRepository.findByRunOffer_Id(id, pageable);
    }

    public ResponseEntity<?> addUserToObjectFromRunOffer(ObjectFromRunOfferCreate request){
        com.zingplay.models.RunOffer runOffer = runOfferRepository.findById(request.getRunOfferId()).orElse(null);

        if(runOffer == null){
            return ResponseEntity.badRequest().body(new MessageResponse("RunOffer exist!!!"));
        }

        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(!(runOffer.getGame().equals(game) && runOffer.getCountry().equals(country))){
            return ResponseEntity.badRequest().body(new MessageResponse("Game & Country not match, contact admin!!!"));
        }
        String idObject = request.getIdObject();
        Object object = objectRepository.findFirstByIdObject(idObject).orElse(null);
        if(object == null){
            LogGameDesignAction.getInstance().error("createObjectFromRunOffer failed idObject exists|{}|{}|{}|{}" , helpers.getUsername(), game, country, idObject);
            return ResponseEntity.badRequest().body(new MessageResponse("Object Id not exists!!"));
        }

        addScanRunOfferToObject(runOffer, object);
        return ResponseEntity.ok(new MessageResponse("Waiting update...and reload to update"));
    }

    private void addScanRunOfferToObject(com.zingplay.models.RunOffer runOffer, Object object) {
        LogGameDesignAction.getInstance().info("addScanRunOfferToObject|{}|{}|{}|{}" , runOffer.getGame(), runOffer.getCountry(), runOffer.getIdRunOffer());
        RunOfferAddToObjectJob job = new RunOfferAddToObjectJob(System.currentTimeMillis());
        job.setIdRunOffer(runOffer.getId());
        job.setIdObject(object.getId());
        job.setRunOffer(runOffer);
        try {
            CronJobServiceImpl.getInstance().addJob(job, false);
        } catch (CronJobException e) {
            e.printStackTrace();
            LogGameDesignAction.getInstance().info("addScanRunOfferToObject|{}|{}|{}|{}|Failed !!!" , runOffer.getGame(), runOffer.getCountry(), runOffer.getIdRunOffer());
        }
    }

    public ResponseEntity<?> createObjectFromRunOffer(ObjectFromRunOfferCreate request){
        com.zingplay.models.RunOffer runOffer = runOfferRepository.findById(request.getRunOfferId()).orElse(null);

        if(runOffer == null){
            return ResponseEntity.badRequest().body(new MessageResponse("RunOffer exist!!!"));
        }

        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(!(runOffer.getGame().equals(game) && runOffer.getCountry().equals(country))){
            return ResponseEntity.badRequest().body(new MessageResponse("Game & Country not match, contact admin!!!"));
        }
        String idObject = request.getIdObject();
        boolean b = objectRepository.existsObjectByIdObject(idObject);
        if(b){
            LogGameDesignAction.getInstance().error("createObjectFromRunOffer failed idObject exists|{}|{}|{}|{}" , helpers.getUsername(), game, country, idObject);
            return ResponseEntity.badRequest().body(new MessageResponse("Object Id had exists!!"));
        }

        Object runOfferObject = runOffer.getObject();
        if(runOfferObject == null){
            runOfferObject = new Object();
        }

        Object objectCreate = new Object();

        objectCreate.setTotalUser(0);
        objectCreate.setGame(game);
        objectCreate.setCountry(country);
        objectCreate.setIdObject(idObject);
        objectCreate.setNameObject(idObject);
        objectCreate.setNote("User buy RunOffer [" + runOffer.getIdRunOffer()+"]");
        objectCreate.setChannelPayments(runOfferObject.getChannelPayments());
        objectCreate.setLastPaidAmounts(runOfferObject.getLastPaidAmounts());
        objectCreate.setPaidTotalMin(runOfferObject.getPaidTotalMin());
        objectCreate.setPaidTotalMax(runOfferObject.getPaidTotalMax());
        objectCreate.setPaidTimesMin(runOfferObject.getPaidTimesMin());
        objectCreate.setPaidTimesMax(runOfferObject.getPaidTimesMax());
        objectCreate.setTotalGameMin(runOfferObject.getTotalGameMin());
        objectCreate.setTotalGameMax(runOfferObject.getTotalGameMax());
        objectCreate.setAgeUserMin(runOfferObject.getAgeUserMin());
        objectCreate.setAgeUserMax(runOfferObject.getAgeUserMax());
        objectCreate.setTimeOnlineMin(runOfferObject.getTimeOnlineMin());
        objectCreate.setTimeOnlineMax(runOfferObject.getTimeOnlineMax());
        objectCreate.setChannelMin(runOfferObject.getChannelMin());
        objectCreate.setChannelMax(runOfferObject.getChannelMax());
        objectCreate.setStatus(Config.SCANNED);
        Object save = objectRepository.save(objectCreate);


        addScanRunOfferToObject(runOffer,save);
        return ResponseEntity.ok(new MessageResponse("Waiting update...and reload to update"));
    }

    public int createAll(List<RunOffer> runOffers) {
        for (RunOffer runOffer : runOffers) {
            runOffer.autoTrim();
        }
        //tao list RunOffer -> override = 1 -> update;
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        List<com.zingplay.models.RunOffer> listSave =  new ArrayList<>();
        Date timeCreate = new Date();
        LogGameDesignAction.getInstance().info("createAll RunOffer creating|{}|{}|{}|{}" , username, game, country, runOffers.size());
        long index = 0;
        for (RunOffer runOffer : runOffers) {
            runOffer.autoTrim();
            String idRunOffer = runOffer.getIdRunOffer();
            String idOffer = runOffer.getIdOffer();
            String idObject = runOffer.getIdObject();
            if(idRunOffer == null || idRunOffer.isEmpty()){
                idRunOffer = "A" + System.currentTimeMillis() + (index ++);
                runOffer.setIdRunOffer(idRunOffer);
            }
            com.zingplay.models.RunOffer runOfferRunning = runOfferRepository.findFirstByIdRunOfferAndGameAndCountry(idRunOffer, game, country).orElse(null);
            if(runOfferRunning != null &&  !runOffer.isOverride()){
                LogGameDesignAction.getInstance().info("createAll RunOffer not override|{}|{}|{}|{}|{}|{}" , username, game, country, idRunOffer, idOffer, idObject);
                continue;
            }
            if(runOfferRunning == null){
                runOfferRunning = new com.zingplay.models.RunOffer();
            }

            Object object = objectRepository.findFirstByIdObject(idObject).orElse(null);
            Offer offer = offerRepository.findFirstByIdOfferAndGameAndCountry(idOffer, game, country).orElse(null);
            if(object == null) {
                LogGameDesignAction.getInstance().info("createAll RunOffer not found object|{}|{}|{}|{}|{}|{}" , username, game, country, idRunOffer, idOffer, idObject);
                continue;
            }
            if(offer == null) {
                LogGameDesignAction.getInstance().info("createAll RunOffer not found offer|{}|{}|{}|{}|{}|{}" , username, game, country, idRunOffer, idOffer, idObject);
                continue;
            }
            LogGameDesignAction.getInstance().info("createAll RunOffer createOrUpdate|{}|{}|{}|{}|{}|{}" , username, game, country, idRunOffer, idOffer, idObject);
            runOfferRunning.setNote(runOffer.getNote());
            runOfferRunning.setTimeCreate(timeCreate);
            runOfferRunning.setTimeUpdate(timeCreate);
            runOfferRunning.setObject(object);
            runOfferRunning.setOffer(offer);
            runOfferRunning.setIdRunOffer(idRunOffer);
            runOfferRunning.setPriority(runOffer.getPriority());
            runOfferRunning.setTimeStart(runOffer.getTimeStart());
            runOfferRunning.setTimeEnd(runOffer.getTimeEnd());
            runOfferRunning.setGame(game);
            runOfferRunning.setCountry(country);
            runOfferRunning.setStatus(Config.WAIT_SCAN);
            runOfferRunning.setScheduleFrequency(runOffer.getScheduleFrequency());
            runOfferRunning.setTimeEndSchedule(runOffer.getTimeEndSchedule());
            runOfferRunning.setPreTimeSetSchedule(runOffer.getPreTimeSetSchedule());
            listSave.add(runOfferRunning);
        }
        List<com.zingplay.models.RunOffer> saveAll = runOfferRepository.saveAll(listSave);
        int size = saveAll.size();
        logService.addLog("import RunOffer from file [" + size +"] record");
        addScanRunOffer(saveAll);
        //cron.addScanRunOffer(saveAll);
        return size;
    }

    private void addScanRunOffer(List<com.zingplay.models.RunOffer> saveAll) {
        for (com.zingplay.models.RunOffer runOffer : saveAll) {
            try {
                addScanRunOffer(runOffer);
            } catch (CronJobException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional (rollbackFor = {})
    public ResponseEntity<?> create(RunOffer runOfferCreate) {
        runOfferCreate.autoTrim();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        LogGameDesignAction.getInstance().info("createRunOffer create|{}|{}|{}|{}" , username, game, country, runOfferCreate.toString());
        runOfferCreate.setGame(game);
        runOfferCreate.setCountry(country);
        String idRunOffer = runOfferCreate.getIdRunOffer();
        if(idRunOffer == null || idRunOffer.isEmpty()){
            runOfferCreate.setIdRunOffer("A" + System.currentTimeMillis());
        }
        try {
            if(runOfferRepository.existsByIdRunOfferAndGameAndCountry(runOfferCreate.getIdRunOffer(),game,country)){
                com.zingplay.models.RunOffer runOffer = runOfferRepository.findFirstByIdRunOfferAndGameAndCountry(runOfferCreate.getIdRunOffer(), game, country).orElse(null);
                if(runOffer != null){
                    LogGameDesignAction.getInstance().info("createRunOffer exist idRunOffer|{}|{}|{}|{}|{}|{}" , username, game, country, runOfferCreate.toString());
                    return ResponseEntity.badRequest().body(new MessageResponse("idRunOffer exist!!"));
                    //return updateRunOffer(runOffer.getId(),runOfferCreate);
                }
            }
            LogGameDesignAction.getInstance().info("createRunOffer created|{}|{}|{}|{}|{}|{}" , username, game, country, runOfferCreate.toString());
            com.zingplay.models.RunOffer runOffer = toRunOfferModel(runOfferCreate);
            if(runOffer.getObject() == null){
                LogGameDesignAction.getInstance().info("createRunOffer object not exist|{}|{}|{}|{}|{}|{}" , username, game, country, runOfferCreate.toString());
                return ResponseEntity.badRequest().body(new MessageResponse("Object not found!!"));
            }
            if(runOffer.getOffer() == null){
                LogGameDesignAction.getInstance().info("createRunOffer offer not exist|{}|{}|{}|{}|{}|{}" , username, game, country, runOfferCreate.toString());
                return ResponseEntity.badRequest().body(new MessageResponse("Offer not found!!"));
            }
            runOffer.setStatus(Config.WAIT_SCAN);
            com.zingplay.models.RunOffer save = runOfferRepository.save(runOffer);
            logService.addLog("create runOffer [" + save.getIdRunOffer() +"]");

            //cron.addScanRunOffer();
            addScanRunOffer(save);
            return ResponseEntity.ok(save);
        } catch (CronJobException e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.badRequest().body(new MessageResponse("Cron job error, contact dev please!"));
        }
    }




    private void addScanRunOffer(com.zingplay.models.RunOffer save) throws CronJobException {
        //if(save.getObject().getStatus() == Config.SCANNED)
        Object object = save.getObject();
        String idObject = "#";
        if(object!=null){
            idObject = object.getIdObject();
        }
        LogSystemAction.getInstance().info("addScanRunOffer|" + save.getIdRunOffer() +"|" + idObject);
        {
            RunOfferJob job = new RunOfferJob(System.currentTimeMillis());
            job.setRunOffer(save);
            job.setIdRunOffer(save.getId());
            try {
                CronJobServiceImpl.getInstance().addJob(job, false);
            }catch (Exception e){
                LogSystemAction.getInstance().error("addScanRunOffer exception|" + save.getIdRunOffer() +"|" + idObject);
                e.printStackTrace();
            }
        }
    }

    public ResponseEntity<?> updateRunOffer(String id, RunOffer runOffer){
        runOffer.autoTrim();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        LogGameDesignAction.getInstance().info("updateRunOffer updating|{}|{}|{}|{}" , username, game, country, runOffer.toString());
        com.zingplay.models.RunOffer runOfferRunning = runOfferRepository.findById(id).orElse(null);
        if(runOfferRunning == null){
            LogGameDesignAction.getInstance().error("updateRunOffer not found runoffer|{}|{}|{}|{}" , username, game, country, runOffer.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("idRunOffer not found!!"));
        }
        Object object1 = runOfferRunning.getObject();
        Object object = objectRepository.findFirstByIdObject(runOffer.getIdObject()).orElse(null);
        Offer offer = offerRepository.findFirstByIdOfferAndGameAndCountry(runOffer.getIdOffer(), game, country).orElse(null);
        if(object == null){
            LogGameDesignAction.getInstance().error("updateRunOffer not found object|{}|{}|{}|{}" , username, game, country, runOffer.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("object not found!!"));
        }
        if(offer == null){
            LogGameDesignAction.getInstance().error("updateRunOffer not found offer|{}|{}|{}|{}" , username, game, country, runOffer.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("offer not found!!"));
        }

        runOfferRunning.setNote(runOffer.getNote());
        runOfferRunning.setTimeUpdate(new Date());
        runOfferRunning.setObject(object);
        runOfferRunning.setOffer(offer);
        runOfferRunning.setPriority(runOffer.getPriority());
        runOfferRunning.setTimeStart(runOffer.getTimeStart());
        runOfferRunning.setTimeEnd(runOffer.getTimeEnd());
        runOfferRunning.setScheduleFrequency(runOffer.getScheduleFrequency());
        runOfferRunning.setPreTimeSetSchedule(runOffer.getPreTimeSetSchedule());
        runOfferRunning.setTimeEndSchedule(runOffer.getTimeEndSchedule());
        boolean needReScan = false;
        if (object != null) {
            if (!object.getId().equals(object1.getId())) {
                needReScan = true;
            }
        } else {
            needReScan = true;
        }
        if (needReScan) {
            runOfferRunning.setStatus(Config.WAIT_RESCAN);
        }
        //cron.addScanRunOffer();
        try {
            com.zingplay.models.RunOffer save = runOfferRepository.save(runOfferRunning);
            addScanRunOffer(save);
            logService.addLog("update RunOffer [" + save.getIdRunOffer() + "]");
        } catch (CronJobException e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.badRequest().body(new MessageResponse("Cron job error, contact dev please!"));
        }
        LogGameDesignAction.getInstance().info("updateRunOffer updated|{}|{}|{}|{}", username, game, country, runOffer.toString());
        return ResponseEntity.ok(runOfferRunning);
    }

    public void deleteRunOffer(String id){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        com.zingplay.models.RunOffer runOffer = runOfferRepository.findById(id).orElse(null);
        if(runOffer != null){
            logService.addLog("delete RunOffer [" + runOffer.getIdRunOffer() +"]");
            LogGameDesignAction.getInstance().info("deleteRunOffer|{}|{}|{}|{}|{}" , username, game, country, runOffer.getIdRunOffer(), id);
        }else{
            LogGameDesignAction.getInstance().info("deleteRunOffer|{}|{}|{}|{}" , username, game, country, id);
        }
        runOfferRepository.deleteById(id);
    }

    private com.zingplay.models.RunOffer toRunOfferModel(RunOffer runOffer){
        com.zingplay.models.RunOffer objUpdate = new com.zingplay.models.RunOffer();

        String country = runOffer.getCountry();
        String game = runOffer.getGame();
        Object object = objectRepository.findFirstByIdObject(runOffer.getIdObject()).orElse(null);
        Offer offer = offerRepository.findFirstByIdOfferAndGameAndCountry(runOffer.getIdOffer(), game, country).orElse(null);
        objUpdate.setIdRunOffer(runOffer.getIdRunOffer());
        objUpdate.setNote(runOffer.getNote());
        objUpdate.setGame(game);
        objUpdate.setCountry(country);
        objUpdate.setObject(object);
        objUpdate.setOffer(offer);
        objUpdate.setPriority(runOffer.getPriority());
        objUpdate.setTimeStart(runOffer.getTimeStart());
        objUpdate.setTimeEnd(runOffer.getTimeEnd());
        objUpdate.setScheduleFrequency(runOffer.getScheduleFrequency());
        objUpdate.setTimeEndSchedule(runOffer.getTimeEndSchedule());
        objUpdate.setPreTimeSetSchedule(runOffer.getPreTimeSetSchedule());

        return objUpdate;
    }

    public void checkNumOfferRunning(String game, String country){
        int numHourLater = 2;
        int rangeMinuteCheck = 60;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + numHourLater);
        Date from = calendar.getTime();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + rangeMinuteCheck);
        Date to = calendar.getTime();

        List<com.zingplay.models.RunOffer> list = runOfferRepository.findAllByGameAndCountryAndTimeStartBeforeAndTimeEndAfter(game, country, to, from);
        if (list.size() == 0){
            String formatAlert = "Game " + game + " [" + country + "] no running offer from " + timeFormat(from) + " to " + timeFormat(to) + "";
            TelegramController.getInstance().sendInfo(TelegramConst.NO_OFFER_RUNNING, game, country, formatAlert);
        }
    }

    public String timeFormat(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        return formatter.format(date);
    }
}
