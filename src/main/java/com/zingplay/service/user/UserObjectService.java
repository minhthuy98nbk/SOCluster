package com.zingplay.service.user;

import com.zingplay.cron.CronBackup;
import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.models.Object;
import com.zingplay.models.RunOffer;
import com.zingplay.models.User;
import com.zingplay.models.UserObject;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UserObjectService {

    private final ObjectRepository objectRepository;
    private final ScheduleObjectRepository scheduleObjectRepository;
    private final UserRepository userRepository;
    private final UserObjectRepository userObjectRepository;
    private final RunOfferRepository runOfferRepository;
    private final UserReceivedRepository userReceivedRepository;
    private final Helpers helpers;
    private final CronBackup cron;
    private final LogService logService;
    @Autowired
    public UserObjectService(ObjectRepository objectRepository, ScheduleObjectRepository scheduleObjectRepository, UserRepository userRepository, UserObjectRepository userObjectRepository, RunOfferRepository runOfferRepository, UserReceivedRepository userReceivedRepository, Helpers helpers, @Lazy CronBackup cron, LogService logService) {
        this.objectRepository = objectRepository;
        this.scheduleObjectRepository = scheduleObjectRepository;
        this.userRepository = userRepository;
        this.userObjectRepository = userObjectRepository;
        this.runOfferRepository = runOfferRepository;
        this.userReceivedRepository = userReceivedRepository;
        this.helpers = helpers;
        this.cron = cron;
        this.logService = logService;
    }

    public Page<?> getAllUserObjectDetail(String id, String search, Pageable pageable){
         String game = helpers.getGame();
         String country = helpers.getCountry();
         if(search!=null && !search.isEmpty()){
             Integer userId = Integer.parseInt(search);

             Page<User> byUserIdContainingAndGameAndCountry = userRepository.findByUserId(userId, pageable);
             List<User> content = byUserIdContainingAndGameAndCountry.getContent();
             Page<UserObject> byUserIsInAndObject_id = userObjectRepository.findByUserIsInAndObject_Id(content, id, pageable);
             return removeUserObjectNull(byUserIsInAndObject_id);

             //return userObjectRepository.findByNameContainingAndGameAndCountryAndObject_Id(search, game, country, id, pageable);
         }
        Page<UserObject> byObject_id = userObjectRepository.findByObject_Id(id, pageable);
        return removeUserObjectNull(byObject_id);
    }

    public ResponseEntity<?> createUserObject(com.zingplay.beans.UserObject request) {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();

        String idObject = request.getIdObject();
        Integer userId = Integer.parseInt(request.getUserId());

        LogGameDesignAction.getInstance().info("createUserObject creating|{}|{}|{}|{}|{}" , username, game, country, userId, idObject);
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId, game, country).orElse(null);
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        if(user == null){
            LogGameDesignAction.getInstance().info("createUserObject create failed user not exist|{}|{}|{}|{}" , username, game, country, userId);
            return ResponseEntity.badRequest().body(new MessageResponse("UserId not exists!!"));
        }
        Object object = objectRepository.findFirstByIdObject(idObject).orElse(null);
        if(object == null){
            LogGameDesignAction.getInstance().info("createUserObject create failed user not exist|{}|{}|{}|{}" , username, game, country, idObject);
            return ResponseEntity.badRequest().body(new MessageResponse("Object not exists!!"));
        }
        UserObject userObject = userObjectRepository.findFirstByUserAndObject(user, object).orElse(null);
        if(userObject != null){
            LogGameDesignAction.getInstance().info("createUserObject create failed user not exist|{}|{}|{}|{}" , username, game, country, idObject);
            return ResponseEntity.badRequest().body(new MessageResponse("User was in object!!"));
        }
        userObject = new UserObject();
        userObject.setUser(user);
        userObject.setObject(object);
        userObject.setTimeCreate(new Date());
        userObject.setTimeUpdate(new Date());
        UserObject save = userObjectRepository.save(userObject);
        logService.addLog("add user [" + user.getUserId() +"] to object [" + object.getIdObject() + "]");

        object.setTotalUser(object.getTotalUser() + 1);
        //if(object.getStatus() == Config.SCANNED)
        {
            Stream<RunOffer> runOfferStream = runOfferRepository.streamAllByObject(object);
            runOfferStream.forEach(runOffer -> {
                runOffer.setCountTotal(object.getTotalUser());
                runOfferRepository.save(runOffer);
            });
        }
        objectRepository.save(object);
        LogGameDesignAction.getInstance().info("createScheduleObject created|{}|{}|{}|{}|{}" , username, game, country, userId, idObject);
        return ResponseEntity.ok().body(save);
    }

    public void deleteUserObject(String id){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        UserObject userObject = userObjectRepository.findById(id).orElse(null);
        if(userObject != null){
            User user = userObject.getUser();
            Object object = userObject.getObject();
            logService.addLog("delete user [" + user.getUserId() +"] out object [" + object.getIdObject() + "]");
            LogGameDesignAction.getInstance().info("deleteUserObject|{}|{}|{}|{}|{}" , username, game, country, user.getUserId(), object.getIdObject());
            Stream<RunOffer> runOfferStream = runOfferRepository.streamAllByObject(object);
            runOfferStream.forEach(userReceivedRepository::deleteAllByRunOffer);

            object.setTotalUser(object.getTotalUser() - 1);
            objectRepository.save(object);
            userObjectRepository.delete(userObject);
        }else{
            LogGameDesignAction.getInstance().info("deleteUserObject|{}|{}|{}|{}" , username, game, country, id);
        }
    }
    public Page<UserObject> getAllObjects(String id, String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        User user = userRepository.findFirstById(id).orElse(null);
        if(search != null && !search.isEmpty()){
            //tim theo object
            Page<Object> byIdObjectContainingAndGameAndCountry = objectRepository.findByIdObjectContaining(search, pageable);
            List<Object> content = byIdObjectContainingAndGameAndCountry.getContent();
            Page<UserObject> allByUserAndObjectIn = userObjectRepository.findAllByUserAndObjectIn(user, content, pageable);
            return removeUserObjectNull(allByUserAndObjectIn);
        }else{
            Page<UserObject> allByUser = userObjectRepository.findAllByUser(user, pageable);

            return removeUserObjectNull(allByUser);
        }
    }

    private Page<UserObject> removeUserObjectNull(Page<UserObject> allByUser) {
        List<UserObject> content = allByUser.getContent();
        Iterator<UserObject> iterator = content.iterator();
        while (iterator.hasNext()) {
            UserObject job = iterator.next();
            if(job.getObject() == null){
                //iterator.remove();
                userObjectRepository.delete(job);
            }
        }
        return allByUser;
    }
}
