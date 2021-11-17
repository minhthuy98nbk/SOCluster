package com.zingplay.service.user;

import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.models.CustomGift;
import com.zingplay.models.Object;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.CustomGiftRepository;
import com.zingplay.repository.ObjectRepository;
import com.zingplay.socket.v3.request.UserReceiveDataCustom;
import com.zingplay.socket.v3.response.TypeCustom;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomDataService {

    private final CustomGiftRepository customGiftRepository;
    private final ObjectRepository objectRepository;
    private final LogService logService;
    private final Helpers helpers;

    public CustomDataService(CustomGiftRepository customGiftRepository, ObjectRepository objectRepository, LogService logService, Helpers helpers) {
        this.customGiftRepository = customGiftRepository;
        this.objectRepository = objectRepository;
        this.logService = logService;
        this.helpers = helpers;
    }

    public Page<?> getAllCustomGift(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            Page<CustomGift> list = customGiftRepository.findByIdContaining(search, pageable);
            if (list.getContent().isEmpty()) {
                list = customGiftRepository.findById(search, pageable);
            }
            return list;
        }
        return customGiftRepository.findAll(pageable);
    }

    public ResponseEntity<?> createCustomGift(CustomGift customGift) {
        return saveCustomGift(customGift, true, "create custom gift");
    }

    public ResponseEntity<?> updateCustomGift(CustomGift updateGift) {
        return saveCustomGift(updateGift, false, "update custom gift");
    }

    public ResponseEntity<?> saveCustomGift(CustomGift customGift, boolean isNew, String log) {
        ResponseEntity<?> inputError = getInputErrorCustomGift(customGift, isNew);
        if (inputError != null) {
            return inputError;
        }
        CustomGift save = customGiftRepository.save(customGift);
        LogGameDesignAction.getInstance().info(log + "|{}|{}|{}|{}", helpers.getUsername(), save);
        logService.addLog(log + " [" + save.getId() + "_" + save.getGiftName() + "]");
        return ResponseEntity.ok().body(save);
    }

    public ResponseEntity<?> getInputErrorCustomGift(CustomGift customGift, boolean isNew) {
        customGift.autoTrim();
        if (customGift.getObjectId() == null) {
            return ResponseEntity.badRequest().body("Object is null!");
        }
        Object object = objectRepository.findById(customGift.getObjectId()).orElse(null);
        if (object == null) {
            return ResponseEntity.badRequest().body("Object not found!");
        }
        customGift.setTotalUser(object.getTotalUser());

        List<CustomGift> oldCustomGifts = customGiftRepository.findByGiftName(customGift.getGiftName());
        if (isNew) {
            if (oldCustomGifts.size() > 0) {
                return ResponseEntity.badRequest().body("Object name duplicate!");
            }
        } else {
            if (customGiftRepository.findById(customGift.getId()).orElse(null) == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Gift id not found!"));
            }
            for (CustomGift gift : oldCustomGifts) {
                if (!gift.getId().equals(customGift.getId())) {
                    return ResponseEntity.badRequest().body("Object name duplicate!");
                }
            }
        }
        if (customGift.getTimeStart() == null ||
                customGift.getTimeEnd() == null ||
                customGift.getTimeStart().getTime() >= customGift.getTimeEnd().getTime()) {
            return ResponseEntity.badRequest().body("Time invalid!");
        }
        return null;
    }

    public ResponseEntity<?> deleteCustomGift(String id) {
        CustomGift customGift = customGiftRepository.findById(id).orElse(null);
        if (customGift == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Gift id not found!"));
        }
        customGiftRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("success"));
    }

    public void updateNumReceiveDataCustom(String game, String country, UserReceiveDataCustom userReceiveDataCustom) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game, country);
        TypeCustom typeCustom = TypeCustom.getTypeByName(userReceiveDataCustom.getTypeCustom());
        switch (typeCustom) {
            case GIFT:
                updateNumReceiveGift(userReceiveDataCustom);
                break;
        }
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }

    public void updateNumReceiveGift(UserReceiveDataCustom userReceiveDataCustom) {
        System.out.println("updateNumReceiveGift " + userReceiveDataCustom);
        CustomGift data = customGiftRepository.findById(userReceiveDataCustom.getDataId()).orElse(null);
        if (data != null) {
            data.setNumReceive(data.getNumReceive() + 1);
            customGiftRepository.save(data);
        }
    }

    public void updateTotalUserCustomGift(String objectId, long totalUser){
        List<CustomGift> customGifts = customGiftRepository.findByObjectIdAndTimeEndIsAfter(objectId, new Date());
        customGifts.forEach(customGift -> customGift.setTotalUser(totalUser));
        customGiftRepository.saveAll(customGifts);
    }
}
