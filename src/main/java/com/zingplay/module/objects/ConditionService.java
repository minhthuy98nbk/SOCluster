package com.zingplay.module.objects;

import com.zingplay.helpers.Helpers;
import com.zingplay.helpers.TrackingHelpers;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.models.Condition;
import com.zingplay.models.ValueCondition;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.service.user.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConditionService {

    private final ConditionRepository conditionRepository;
    private final Helpers helpers;
    private final LogService logService;
    private List<String> gameAddCondition;

    @Autowired
    public ConditionService(ConditionRepository offerRepository, Helpers helpers, LogService logService) {
        this.conditionRepository = offerRepository;
        this.helpers = helpers;
        this.logService = logService;
        this.gameAddCondition = new ArrayList<>();
    }

    public Page<?> getAllCondition(String search, Pageable pageable) {
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if (search != null && !search.isEmpty()) {
            Page<com.zingplay.models.Condition> byIdOfferContainingAndGameAndCountry = conditionRepository.findByNameContainingOrKeyContaining(search, search, pageable);
            if (!byIdOfferContainingAndGameAndCountry.getContent().isEmpty()) {
                return byIdOfferContainingAndGameAndCountry;
            }
        }
        if (!gameAddCondition.contains(game)) {
            addAllRuntimeCondition(game);
            gameAddCondition.add(game);
        }
        Page<?> res = conditionRepository.findAll(pageable);
        return res;
    }

    public ResponseEntity<?> createAll(List<Condition> conditions) {
        //1. check permission
        //2. valid data
        //3. check exist
        //4. create

        //2.
        for (Condition condition : conditions) {
            ResponseEntity<?> errorInput = this.getErrorInput(condition);
            if (errorInput != null) {
                return errorInput;
            }
        }

        //3.
        List<String> keys = conditions.stream().map(Condition::getKey).collect(Collectors.toList());
        boolean exists = conditionRepository.existsConditionByKeyIn(keys);
        if (exists) {
            List<Condition> byKeyIn = conditionRepository.findByKeyIn(keys);
            StringBuilder keyStr = new StringBuilder();
            for (Condition condition : byKeyIn) {
                keyStr.append((keyStr.length() == 0) ? "" : ", ").append(condition.getKey());
            }
            return ResponseEntity.badRequest().body("Condition with key [" + keys + "] was exists!");
        }

        //4.
        List<Condition> saved = conditionRepository.saveAll(conditions);
        String username = helpers.getUsername();
        String game = helpers.getGame();
        for (int i = 0; i < saved.size(); i++) {
            Condition condition = saved.get(i);
            LogGameDesignAction.getInstance().info("create |{}|{}|{}", username, i, condition);
            logService.addLog("create |[" + condition + "]");
            ConditionController.getInstance().addOrUpdateCondition(game, condition);
        }
        LogGameDesignAction.getInstance().info("create condition total create|" + saved.size());
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<?> create(Condition condition) {
        //2.
        ResponseEntity<?> errorInput = this.getErrorInput(condition);
        if (errorInput != null) {
            return errorInput;
        }
        //3.
        List<String> keys = Collections.singletonList(condition.getKey());
        boolean exists = conditionRepository.existsConditionByKeyIn(keys);
        if (exists) {
            return ResponseEntity.badRequest().body("Condition with key [" + condition.getKey() + "] was exists!");
        }

        //4.
        Condition saved = conditionRepository.save(condition);
        String username = helpers.getUsername();
        String game = helpers.getGame();
        LogGameDesignAction.getInstance().info("create |{}|0|{}", username, condition);
        logService.addLog("create |[" + condition + "]");
        ConditionController.getInstance().addOrUpdateCondition(game, condition);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<?> updateCondition(String id, Condition condition) {
        //1. check valid data
        //2. check exist
        //3. update


        //1.
        //2.
        ResponseEntity<?> errorInput = this.getErrorInput(condition);
        if (errorInput != null) {
            return errorInput;
        }

        //2.
        Condition conditionFind = conditionRepository.findById(id).orElse(null);
        if (conditionFind == null) {
            return ResponseEntity.badRequest().body("Condition with id [" + condition.getId() + "] not found to update!");
        }

        conditionFind.setKey(condition.getKey());
        conditionFind.setName(condition.getName());
        conditionFind.setType(condition.getType());
        conditionFind.setSamples(condition.getSamples());
        conditionFind.setSubConditions(condition.getSubConditions());
        conditionFind.setSubSamples(condition.getSubSamples());
        String username = helpers.getUsername();
        String game = helpers.getGame();
        LogGameDesignAction.getInstance().error("update |{}|{}", username, condition);
        logService.addLog("update [" + condition + "]");
        ConditionController.getInstance().addOrUpdateCondition(game, condition);

        //update condition vao controller để check quet object
        return ResponseEntity.ok(conditionRepository.save(conditionFind));
    }

    public ResponseEntity<?> getErrorInput(Condition condition) {
        condition.setId(null);
        condition.setName(condition.getName().trim());
        condition.setKey(condition.getKey().trim());
        condition.setType(condition.getType().trim());
        switch (condition.getType()) {
            case ConditionConfig.STRING:
                break;
            case ConditionConfig.LONG:
            case ConditionConfig.DURATION:
                try {
                    TrackingHelpers.convertToLongSamples(condition.getSamples());
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Condition [" + condition.getName() + "] samples is not " + condition.getType() + "!");
                }
                break;
            case ConditionConfig.FLOAT:
                try {
                    TrackingHelpers.convertToFloatSamples(condition.getSamples());
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Condition [" + condition.getName() + "] samples is not " + condition.getType() + "!");
                }
                break;
            case ConditionConfig.OBJECT:
                try {
                    List<ValueCondition> valueConditionList = TrackingHelpers.convertToObjectSample(condition.getSubConditions(), condition.getSubSamples());
                    if (valueConditionList == null) {
                        return ResponseEntity.badRequest().body("Condition [" + condition.getName() + "] samples object is not valid!");
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Condition [" + condition.getName() + "] samples is not " + condition.getType() + "!");
                }
                break;
            default:
                return ResponseEntity.badRequest().body("Condition with name [" + condition.getName() + "] type wrong!");
        }
        return null;
    }

    public ResponseEntity<?> deleteCondition(String id) {
        Condition condition = conditionRepository.findById(id).orElse(null);
        if (condition == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("not found condition with id " + id));
        }
        String game = helpers.getGame();
        String username = helpers.getUsername();
        ConditionController.getInstance().removeCondition(game, condition);
        conditionRepository.deleteById(id);
        LogGameDesignAction.getInstance().error("delete |{}|{}", username, condition);
        logService.addLog("delete [" + condition + "]");

        return ResponseEntity.ok(new MessageResponse("success"));
    }

    public void addAllRuntimeCondition(String game) {
        List<Condition> conditionList = conditionRepository.findAll();
        for (Condition condition : conditionList) {
            ConditionController.getInstance().addOrUpdateCondition(game, condition);
        }
    }
}
