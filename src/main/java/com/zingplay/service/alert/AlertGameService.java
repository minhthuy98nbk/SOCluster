package com.zingplay.service.alert;

import com.zingplay.models.AlertGameInfo;
import com.zingplay.repository.AlertGameInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by thuydtm on 9:21 PM 7/10/2021
 */
@Service
public class AlertGameService {

    @Value("${alertConfig.defaultRateUp}")
    public float defaultRateUp;

    @Value("${alertConfig.defaultRateDown}")
    public float defaultRateDown;

    @Value("${alertConfig.defaultMinuteCountLog}")
    public long defaultMinuteCountLog;

    @Value("${alertConfig.defaultMinutePastCheck}")
    public long defaultMinutePastCheck;

    public static final Logger logger = LoggerFactory.getLogger(AlertGameService.class);

    @Autowired
    AlertGameInfoRepository repository;

    private static AlertGameService instance = new AlertGameService();

    public static AlertGameService getInstance() {
        return instance;
    }

    @Autowired
    public AlertGameService() {
        instance = this;
    }

    final long MINUTE_PER_HOUR = 60;

    public ResAlertConfig updateInfo(String game, int type, float value) {
        AlertGameInfo oldGameInfo = getConfig(game);
        ResAlertConfig resAlertConfig = new ResAlertConfig(false, "Fail", oldGameInfo);
        if (value <= 0) {
            return resAlertConfig;
        }
        switch (type){
            case 1:
                if (value <= 1){
                    resAlertConfig.setMsg("Rate up must be greater than 1");
                    return resAlertConfig;
                }
                oldGameInfo.setRateUp(value);
                break;
            case 2:
                if (value <= 0 || value >= 1){
                    resAlertConfig.setMsg("Rate down must be greater than 0 and less than 1 ");
                    return resAlertConfig;
                }
                oldGameInfo.setRateDown(value);
                break;
            case 3:
                if (value > MINUTE_PER_HOUR || MINUTE_PER_HOUR % value != 0 || value > oldGameInfo.getMinutePastCheck()) {
                    return resAlertConfig;
                }
                oldGameInfo.setMinuteCountLog((long) value);
                break;
            case 4:
                if (value < oldGameInfo.getMinuteCountLog() || value % oldGameInfo.getMinuteCountLog() != 0) {
                    return resAlertConfig;
                }
                oldGameInfo.setMinutePastCheck((long) value);
                break;
            default:
                return null;
        }
        resAlertConfig.setAlertGameInfo(oldGameInfo);
        resAlertConfig.setSuccess(true);
        resAlertConfig.setMsg("Success");
        repository.save(oldGameInfo);
        return resAlertConfig;
    }

    public AlertGameInfo getConfig(String game) {
        try {
            List<AlertGameInfo> all = repository.findAll();
            if (all.size() > 0){
                return all.get(0);
            }
            return repository.save(new AlertGameInfo(game));
        } catch (Exception e) {
            logger.info("Exception get alert by game ", game, e);
            AlertGameInfo exceptionInfo = new AlertGameInfo(game);
            exceptionInfo.setRateUp(-1);
            exceptionInfo.setRateDown(-1);
            return new AlertGameInfo(game);
        }
    }


}
