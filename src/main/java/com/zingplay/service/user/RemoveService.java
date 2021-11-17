package com.zingplay.service.user;

import com.zingplay.beans.RemoveInfo;
import com.zingplay.constant.DateTimeConstant;
import com.zingplay.cron.CronJobServiceImpl;
import com.zingplay.cron.beans.job.RemoveOldUserJob;
import com.zingplay.cron.beans.job.ScanUserGroupJob;
import com.zingplay.cron.exception.CronJobException;
import com.zingplay.enums.Config;
import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogSystemAction;
import com.zingplay.models.Object;
import com.zingplay.models.User;
import com.zingplay.module.report.v1.ReportBuyOfferRepository;
import com.zingplay.module.telegram.MyPair;
import com.zingplay.repository.*;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

@Service
public class RemoveService {

    private final LogRepository logRepository;
    private final ObjectRepository objectRepository;
    private final OfferRepository offerRepository;
    private final ReportBuyOfferRepository reportBuyOfferRepository;
    private final RunOfferRepository runOfferRepository;
    private final ScheduleObjectRepository scheduleObjectRepository;
    private final UserBuyOfferRepository userBuyOfferRepository;
    private final UserObjectRepository userObjectRepository;
    private final UserRepository userRepository;
    private final UserReceivedRepository userReceivedRepository;
    private final LogService logService;

    private final Helpers helpers;
    private static RemoveService _instance;

    private final Map<String, RemoveInfo> removeJobInfo = new HashMap<>();

    public static RemoveService getInstance(){
        return _instance;
    }

    @Autowired
    public RemoveService(ObjectRepository objectRepository, OfferRepository offerRepository, ScheduleObjectRepository scheduleObjectRepository, UserRepository userRepository, UserObjectRepository userObjectRepository, RunOfferRepository runOfferRepository, Helpers helpers, LogService logService, LogRepository logRepository, ReportBuyOfferRepository reportBuyOfferRepository, @Lazy UserBuyOfferRepository userBuyOfferRepository, UserReceivedRepository userReceivedRepository) {
        this.objectRepository = objectRepository;
        this.offerRepository = offerRepository;
        this.scheduleObjectRepository = scheduleObjectRepository;
        this.userRepository = userRepository;
        this.userObjectRepository = userObjectRepository;
        this.runOfferRepository = runOfferRepository;
        this.logService = logService;
        this.helpers = helpers;
        this.logRepository = logRepository;
        this.reportBuyOfferRepository = reportBuyOfferRepository;
        this.userBuyOfferRepository = userBuyOfferRepository;
        this.userReceivedRepository = userReceivedRepository;
        _instance = this;
    }

    public void removeAllObject() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        Stream<Object> objectStream = objectRepository.findAll().stream();
        objectStream.forEach(userObjectRepository::deleteAllByObject);
        objectRepository.deleteAll();
        runOfferRepository.deleteAllByGameAndCountry(game,country);
        logService.addLog("Delete all object");
    }

    public void removeAllOffer() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        runOfferRepository.deleteAllByGameAndCountry(game,country);
        offerRepository.deleteAllByGameAndCountry(game,country);
        logService.addLog("Delete all offer");
    }

    public void removeAllUser() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        Stream<User> userStream = userRepository.findAll().stream();
        userStream.forEach(user -> {
            userBuyOfferRepository.deleteAllByUser(user);
            userReceivedRepository.deleteAllByUser(user);
            userObjectRepository.deleteAllByUser(user);
        });
        userRepository.deleteAll();
        logService.addLog("Delete all user");

    }

    public void removeAll() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        logRepository.deleteAllByGameAndCountry(game,country);
        offerRepository.deleteAllByGameAndCountry(game,country);
        reportBuyOfferRepository.deleteAllByGameAndCountry(game,country);

        runOfferRepository.deleteAllByGameAndCountry(game,country);
        scheduleObjectRepository.deleteAllByGameAndCountry(game,country);

        Stream<Object> objectStream = objectRepository.findAll().stream();
        objectStream.forEach(userObjectRepository::deleteAllByObject);
        objectRepository.deleteAll();

        Stream<User> userStream = userRepository.findAll().stream();
        userStream.forEach(user -> {
            userBuyOfferRepository.deleteAllByUser(user);
            userReceivedRepository.deleteAllByUser(user);
            userObjectRepository.deleteAllByUser(user);
        });
        userRepository.deleteAll();
        logService.addLog("Delete all user,offer,object,runOffer");
    }

    public void removeAlls() {
        logRepository.deleteAll();
        objectRepository.deleteAll();
        offerRepository.deleteAll();
        reportBuyOfferRepository.deleteAll();
        runOfferRepository.deleteAll();
        scheduleObjectRepository.deleteAll();
        userBuyOfferRepository.deleteAll();
        userObjectRepository.deleteAll();
        userObjectRepository.deleteAll();
        userRepository.deleteAll();
        logService.addLog("Delete all db");
    }

    public void removeAllLog() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        logRepository.deleteAllByGameAndCountry(game,country);
        logService.addLog("Delete all log");
    }

    /**
     * xoa tat ca user co lastDateLogin > date
     */

    public RemoveInfo getRemoveUserScheduleInfo() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();

        String removeId = this.getRemoveId(game, country);
        RemoveInfo removeInfo = removeJobInfo.get(removeId);
        if (removeInfo == null) {
            return null;
        }
        long count = userRepository.countByTimeOnlineBefore(removeInfo.getLastTimeOnline());
        removeInfo.setCurOldUser(count);
        LogSystemAction.getInstance().info("getRemoveUserScheduleInfo | removeInfo > " + removeInfo);
        return removeInfo;
    }

    public boolean cancelRemoveUserSchedule() {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();

        String removeId = this.getRemoveId(game, country);
        RemoveInfo removeInfo = removeJobInfo.get(removeId);
        if (removeInfo == null) {
            return false;
        }
        LogSystemAction.getInstance().info("cancelRemoveUserSchedule | removeInfo > " + removeInfo);
        removeJobInfo.put(removeId, null);
        return true;
    }

    public MyPair<Boolean, String> removeAllOldUser(int date, int numRemovePerJob, int numMinuteDelay, int scheduleDelay) {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        //xóa -> UserReceived
        //xóa -> UserObject
        //xóa -> UserBuyOffer
        //xóa -> User
        String removeId = this.getRemoveId(game, country);
        if (removeJobInfo.get(removeId) != null) {
            return new MyPair<>(false, "Old dchedule remove old user is running");
        }

        // time remove
        Calendar timeRemove = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("+7")));
        Date now = timeRemove.getTime();

        // get last time onl
        Calendar instance = Calendar.getInstance();
        instance.setTime(now);
        instance.add(Calendar.DATE, -date);
        Date lasTimeOnline = instance.getTime();
        LogSystemAction.getInstance().info("Delete oldUser lastTimeOnline > " + lasTimeOnline + " | timeStartRemove > " + now);

        long count = userRepository.countByTimeOnlineBefore(lasTimeOnline);
        if (count <= 0) {
            return new MyPair<>(false, "No old user to remove");
        }

        // save ram data
        RemoveInfo removeInfo = new RemoveInfo(now, lasTimeOnline, numRemovePerJob, numMinuteDelay, scheduleDelay);
        //removeInfo.setNumUserCountFirstTime(count);
        removeInfo.setStatus(Config.WAIT_SCAN);
        this.removeJobInfo.put(removeId, removeInfo);

        // add first job
        try {
            RemoveOldUserJob job = new RemoveOldUserJob(timeRemove.getTime().getTime(), now,  game, country, username);
            CronJobServiceImpl.getInstance().addJob(job, false);
        } catch (CronJobException e) {
            LogSystemAction.getInstance().error("Delete oldUser lastTimeOnline > " + lasTimeOnline + " | timeStartRemove > " + now + " | EXCEPTION");
            e.printStackTrace();
        }
        return new MyPair<>(true, "Schedule remove " + count + " old user success!");
    }

    public long removeAmountOldUser(Date timeStartRemove, String gameID, String country, String username) {
        //xóa -> UserReceived
        //xóa -> UserObject
        //xóa -> UserBuyOffer
        //xóa -> User
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(gameID, country);

        // check
        String removeId = this.getRemoveId(gameID, country);
        RemoveInfo removeInfo = removeJobInfo.get(removeId);
        if (removeInfo == null || removeInfo.getTimeStartRemove() != timeStartRemove) {
            return -1;
        }
        Date lasTimeOnline = removeInfo.getLastTimeOnline();
        int numRemove = removeInfo.getNumRemovePerJob();

        // remove
        LogSystemAction.getInstance().info("Delete UserReceived UserObject UserBuyOffer User lastTimeOnline > " + lasTimeOnline + " | timeStartRemove > " + timeStartRemove);
        Pageable pageable = PageRequest.of(0, numRemove);
        Page<User> users = userRepository.findByTimeOnlineBefore(lasTimeOnline, pageable);
        numRemove = (int) Math.min(users.stream().count(), numRemove);
        LogSystemAction.getInstance().info("Delete UserReceived UserObject UserBuyOffer User lastTimeOnline > " + lasTimeOnline + " | numRemove " + numRemove);
        for (User user : users){
            LogSystemAction.getInstance().info("removeAllOldUser|" + user);
            userReceivedRepository.deleteAllByUser(user);
            userObjectRepository.deleteAllByUser(user);
            userBuyOfferRepository.deleteAllByUser(user);
            userRepository.deleteById(user.getId());
        }
        logService.addLog("Delete UserReceived UserObject UserBuyOffer User lastTimeOnline > " + lasTimeOnline + " | numRemove " + numRemove + " | END", gameID, country, username);

        // save remove info on ram
        Calendar timeRemove = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("+7")));
        Date now = timeRemove.getTime();
        removeInfo.getJobInfo().put(now, numRemove);
        removeInfo.setNumUserRemoved(removeInfo.getNumUserRemoved() + numRemove);
        removeInfo.setStatus(Config.SCANNING);
        // add next job
        long count = userRepository.countByTimeOnlineBefore(removeInfo.getLastTimeOnline());
        if (count > 0) {
            try {
                timeRemove.set(Calendar.MINUTE, (int) (timeRemove.get(Calendar.MINUTE) + removeInfo.getMinuteDelay()));
                RemoveOldUserJob job = new RemoveOldUserJob(timeRemove.getTime().getTime(), removeInfo.getTimeStartRemove(), gameID, country, username);
                System.out.println("next remove time " + timeRemove);
                CronJobServiceImpl.getInstance().addJob(job, false);
            } catch (CronJobException e) {
                LogSystemAction.getInstance().error("Delete oldUser lastTimeOnline > " + lasTimeOnline + " | timeStartRemove > " + now + " | EXCEPTION");
                e.printStackTrace();
            }
        } else if(removeInfo.getScheduleDelay() > 0){
            removeInfo.getJobInfo().clear();
            removeInfo.setCurOldUser(0);
            Calendar instance =Calendar.getInstance();

            instance.setTime(removeInfo.getTimeStartRemove());
            instance.add(Calendar.DATE, removeInfo.getScheduleDelay());
            removeInfo.setTimeStartRemove(instance.getTime());

            instance.setTime(removeInfo.getLastTimeOnline());
            instance.add(Calendar.DATE, removeInfo.getScheduleDelay());
            removeInfo.setLastTimeOnline(instance.getTime());

            removeInfo.setStatus(Config.WAIT_RESCAN);
            try {
                RemoveOldUserJob job = new RemoveOldUserJob(removeInfo.getTimeStartRemove().getTime(),
                        removeInfo.getTimeStartRemove(), gameID, country, username);
                System.out.println("next remove time " + timeRemove);
                CronJobServiceImpl.getInstance().addJob(job, false);
            } catch (CronJobException e) {
                LogSystemAction.getInstance().error("Delete oldUser lastTimeOnline > " + lasTimeOnline + " | timeStartRemove > " + now + " | EXCEPTION");
                e.printStackTrace();
            }
        } else{
            removeInfo.setStatus(Config.SCANNED);
        }
        removeJobInfo.replace(removeId, removeInfo);
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
        return numRemove;
    }

    public String getRemoveId(String game, String country){
        return game + "_" + country;
    }

}
