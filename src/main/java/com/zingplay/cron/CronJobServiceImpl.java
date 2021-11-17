package com.zingplay.cron;

import com.mongodb.client.MongoClient;
import com.zingplay.beans.CronProcess;
import com.zingplay.cron.beans.group_jobs.GroupJob;
import com.zingplay.cron.beans.job.ACronJob;
import com.zingplay.cron.beans.job.RunOfferJob;
import com.zingplay.cron.beans.job.ScanUserGroupJob;
import com.zingplay.cron.exception.CronJobException;
import com.zingplay.cron.exception.CronJobGroupIdInvalidException;
import com.zingplay.cron.exception.CronJobServiceNotReadyException;
import com.zingplay.enums.Config;
import com.zingplay.log.LogSystemAction;
import com.zingplay.models.*;
import com.zingplay.models.Object;
import com.zingplay.module.objects.ConditionController;
import com.zingplay.module.objects.ConditionRepository;
import com.zingplay.repository.RunOfferRepository;
import com.zingplay.repository.ScheduleObjectRepository;
import com.zingplay.scheduler.CoreTaskScheduler;
import com.zingplay.service.user.SystemService;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CronJobServiceImpl implements ICronJobService {
    private static CronJobServiceImpl instance;

    private static final int THREAD_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 1;

    private static AtomicBoolean READY_FLAG = new AtomicBoolean(false);

    private Lock lock = new ReentrantLock();
    private ThreadPoolExecutor jobScheduler = new ThreadPoolExecutor(THREAD_POOL_SIZE, MAX_POOL_SIZE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
    private Map<Integer, GroupJob> mapGroupJob = new ConcurrentHashMap<>();

    private ScheduleObjectRepository scheduleObjectRepository;
    private RunOfferRepository runOfferRepository;
    private ConditionRepository conditionRepository;
    private MongoClient mongoClient;
    private Map<String, String> mapGame_Version = new HashMap<>();

    @Autowired
    public CronJobServiceImpl(ScheduleObjectRepository scheduleObjectRepository, RunOfferRepository runOfferRepository, ConditionRepository conditionRepository, MongoClient mongoClient) {
        this.scheduleObjectRepository = scheduleObjectRepository;
        this.runOfferRepository = runOfferRepository;
        this.conditionRepository = conditionRepository;
        this.mongoClient = mongoClient;
        instance = this;
    }

    public void addGameVersion(String game, String version){
        mapGame_Version.put(game, version);
    }

    public String getVersion(String game){
        return mapGame_Version.getOrDefault(game, "");
    }

    public Map<String , String> getMapGame_Version(){
        return mapGame_Version;
    }

    public static CronJobServiceImpl getInstance() {
        return instance;
    }

    @EventListener
    public void wake(ApplicationReadyEvent event) {
        SystemService.getInstance().createIndex();
        reloadJobsFromDB();
        run();
        READY_FLAG.set(true);
    }

    private void run() {
        Runnable run = new LoopTask();
        CoreTaskScheduler.getInstance().scheduleTask(run, 0, 1, TimeUnit.SECONDS);
    }

    private void reloadJobsFromDB() {
        //load all lich quet user
        List<Integer> list = Arrays.asList(Config.WAIT_SCAN,Config.WAIT_RESCAN,Config.SCANNING);
        Date curTime = new Date();

        mongoClient.listDatabaseNames().forEach(s -> {
            if(!s.startsWith("dbOffer_")) return;
            LogSystemAction.getInstance().info("reloadJobsFromDB|" + s);
            String[] split = s.split("_");
            if(split.length < 3) return;
            String game = split[1];
            String country = split[2];
            MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
            //List<ScheduleObject> all = scheduleObjectRepository.findAllByTimeScanLessThanEqualAndStatusIn(curTime, list);
            List<ScheduleObject> all = scheduleObjectRepository.findAllByStatusIn(list);
            if(all != null) {
                for (ScheduleObject scheduleObject : all) {
                    ScanUserGroupJob job = new ScanUserGroupJob(scheduleObject.getTimeScan().getTime());
                    job.setScheduleObject(scheduleObject);
                    job.setIdScheduleObject(scheduleObject.getId());
                    try {
                        addJob(job, true);
                        Object object = scheduleObject.getObject();
                        if(object != null){
                            LogSystemAction.getInstance().info("reloadJobsFromDB scheduleObject|{}|{}|{}|{}|{}" , scheduleObject.getGame(),scheduleObject.getCountry(), object.getIdObject(), scheduleObject.getName(), scheduleObject.getTimeScan());
                        }
                    } catch (CronJobException e) {
                        LogSystemAction.getInstance().error("reloadJobsFromDB scheduleObject exception |{}|{}|{}|{}|{}" , scheduleObject.getGame(),scheduleObject.getCountry(), scheduleObject.getName(), scheduleObject.getTimeScan());
                        e.printStackTrace();
                    }
                }
            }

            // add runtime condition
            List<Condition> conditionList =  conditionRepository.findAll();
            for (Condition condition : conditionList){
                ConditionController.getInstance().addOrUpdateCondition(game, condition);
            }

            List<RunOffer> allRunOffer = runOfferRepository.findAllByStatusIn(list);
            if(allRunOffer != null) {
                for (RunOffer runOffer : allRunOffer) {
                    RunOfferJob job = new RunOfferJob(curTime.getTime());
                    job.setRunOffer(runOffer);
                    job.setIdRunOffer(runOffer.getId());
                    try {
                        addJob(job, true);
                        Offer offer = runOffer.getOffer();
                        Object object = runOffer.getObject();
                        if(offer == null){
                            LogSystemAction.getInstance().info("reloadJobsFromDB scheduleObject|{}|{}|{}|{}|{}" , runOffer.getGame(),runOffer.getCountry(), object.getIdObject(), null);
                        }else if(object == null){
                            LogSystemAction.getInstance().info("reloadJobsFromDB scheduleObject|{}|{}|{}|{}|{}" , runOffer.getGame(),runOffer.getCountry(), null, offer.getIdOffer());
                        }else {
                            LogSystemAction.getInstance().info("reloadJobsFromDB scheduleObject|{}|{}|{}|{}|{}" , runOffer.getGame(),runOffer.getCountry(), object.getIdObject(), offer.getIdOffer());
                        }
                    } catch (CronJobException e) {
                        e.printStackTrace();
                    }
                }
            }
            MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
        });


    }

    @Override
    public boolean addJob(ACronJob cronJob, boolean force) throws CronJobException {
        if(!force){
            checkServiceAvailable();
        }

        GroupJob groupJob = null;
        try {
            lock.lock();
            groupJob = mapGroupJob.get(cronJob.getGroupId());
            if (groupJob == null) {
                groupJob = new GroupJob(cronJob.getGroupId());
                mapGroupJob.put(cronJob.getGroupId(), groupJob);
            }
            groupJob.put(cronJob);
        } catch (Exception e) {
            // write logs
            LogSystemAction.getInstance().error("addJob exception ... need checking.");
            e.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }

        return true;
    }

    @Override
    public boolean removeJob(Integer jobId) throws CronJobException {
        checkServiceAvailable();
        try {
            lock.lock();
            for(GroupJob groupJob : mapGroupJob.values()) {
                return groupJob.removeJob(jobId);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeGroupJobs(int groupId) throws CronJobException {
        checkServiceAvailable();
        try {
            lock.lock();
            GroupJob groupJob = mapGroupJob.remove(groupId);
            if(groupJob == null) {
                throw new CronJobGroupIdInvalidException();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int countTotalJobs() throws CronJobException {
        checkServiceAvailable();

        int result = 0;
        for(GroupJob groupJob : mapGroupJob.values()) {
            result += groupJob.size();
        }
        return result;
    }

    @Override
    public int countGroupJobs(int groupId) throws CronJobException {
        checkServiceAvailable();

        GroupJob groupJob = mapGroupJob.get(groupId);
        if(groupJob == null) {
            throw new CronJobGroupIdInvalidException();
        }
        return groupJob.size();
    }

    private void checkServiceAvailable() throws CronJobException {
        if(!READY_FLAG.get()) {
            throw new CronJobServiceNotReadyException();
        }
    }

    public Page<?> getCronProcess(int groupId, Pageable pageable){
        GroupJob groupJob = mapGroupJob.get(groupId);
        Page<?> res ;
        List<CronProcess> listRes = new ArrayList<>();
        int startIdx = (pageable.getPageNumber() < 0? 0: pageable.getPageNumber())*pageable.getPageSize();
        int size=  pageable.getPageSize();
        int total = 0;
        if(groupJob != null){
            java.lang.Object[] jobArray = groupJob.getQueueJob().toArray();
            total = jobArray.length;
            int max = Math.min(startIdx + size, jobArray.length) ;
            for(int i = startIdx; i< max; i++){
                ACronJob job = (ACronJob) jobArray[i];
                CronProcess process = toCronProcess(job);
                listRes.add(process);
            }
        }
        res = new PageImpl<>(listRes, pageable, total);
        return res;
    }

    private CronProcess toCronProcess(ACronJob aCronJob) {
        CronProcess process= new CronProcess();
        process.setId(aCronJob.getId());
        process.setGroupId(aCronJob.getGroupId());
        process.setTimeStart(aCronJob.getTimeStart());
        process.setStatus(aCronJob.getStatus());
        process.setProcessRecord(aCronJob.getProcessRecord());
        process.setIdProcess(aCronJob.getIdJob());
        process.setGame(aCronJob.getGame());
        process.setCountry(aCronJob.getCountry());
        return process;
    }

    private class LoopTask implements Runnable {

        @Override
        public void run() {
            try {
                lock.lock();
                List<Integer> listEmptyGroupJob = new ArrayList<>();
                // do job
                for(GroupJob groupJob : mapGroupJob.values()) {
                    ACronJob job = groupJob.getFirstJob();
                    if(job == null) {
                        continue;
                    }

                    if(job.getTimeStart() <= System.currentTimeMillis() && groupJob.getActionLock().tryLock()) {
                        // unlock current thread -> lock in the sub thread
                        groupJob.getActionLock().unlock();
                        job.initCallBack(groupJob.getActionLock());
                        try {
                            jobScheduler.execute(job);
                            groupJob.removeFirstJob();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(groupJob.isEmpty()) {
                            listEmptyGroupJob.add(groupJob.getId());
                        }
                    }
                }
                // remove empty job
                for(Integer groupId : listEmptyGroupJob) {
                    mapGroupJob.remove(groupId);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
