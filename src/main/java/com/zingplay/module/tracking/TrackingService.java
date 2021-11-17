package com.zingplay.module.tracking;

import com.zingplay.models.AlertGameInfo;
import com.zingplay.module.telegram.TelegramConst;
import com.zingplay.module.telegram.TelegramController;
import com.zingplay.service.alert.AlertGameService;
import com.zingplay.socket.SocketConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

@Service
public class TrackingService {

    public final long SECOND_PER_MINUTE = 60;
    public final long MINUTE_PER_HOUR = 60;
    public final long HOUR_PER_DAY = 24;

    private final TrackingRepository trackingRepository;

    @Autowired
    public TrackingService(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    /**
     * INC NUM LOG TRACKING
     */

    public void incrementTracking(String game, String country, long time, String action) {
        // get time
        if (time < 99999999999L) {
            time = time * 1000;
        }
        Date timeCreate = new Date(time);
        // getTracking
        Tracking tracking = getTracking(game, country, timeCreate);
        // new count
        incrementTrackingNew(game, tracking, action, timeCreate);
        // old count
        incrementTrackingOld(tracking, action);
        // save
        autoSave(tracking);
    }

    public void incrementTrackingNew(String game, Tracking tracking, String action, Date timeCreate) {
        // get time
        OffsetDateTime offsetDateTime = getOffsetTime(timeCreate);
        int hour = offsetDateTime.getHour();
        int minute = offsetDateTime.getMinute();
        // get map hour
        if (!tracking.getCountDetailLog().containsKey(hour)) {
            tracking.getCountDetailLog().put(hour, new HashMap<>());
            AlertGameInfo config = AlertGameService.getInstance().getConfig(game);
            if (config == null) {
                return;
            }
            int n = (int) (MINUTE_PER_HOUR / config.getMinuteCountLog());
            tracking.getNumPartByHour().put(hour, n);
        }
        HashMap<String, long[]> hourLogs = tracking.getCountDetailLog().get(hour);
        // get map action
        if (!hourLogs.containsKey(action) || hourLogs.get(action).length == 0) {
            hourLogs.put(action, new long[tracking.getNumPartByHour().get(hour)]);
        }
        long[] actionLogs = hourLogs.get(action);
        // inc part
        int idxPart = getIdxPart(tracking, hour, minute);
        actionLogs[idxPart]++;
    }

    public void incrementTrackingOld(Tracking tracking, String action) {
        switch (action) {
            case SocketConst.ACTION_LOGIN:
                tracking.setCountLogin(tracking.getCountLogin() + 1);
                break;
            case SocketConst.ACTION_STATS_GAME:
                tracking.setCountState(tracking.getCountState() + 1);
                break;
            case SocketConst.ACTION_USER_REQUEST_OFFERS:
                tracking.setCountRequest(tracking.getCountRequest() + 1);
                break;
            case SocketConst.ACTION_USER_PAYMENT:
                tracking.setCountPayment(tracking.getCountPayment() + 1);
                break;
            case SocketConst.ACTION_USER_BUY_OFFER:
                tracking.setCountBuy(tracking.getCountBuy() + 1);
                break;
            case SocketConst.ACTION_GET_DATA_CUSTOM:
                tracking.setCountDataCustom(tracking.getCountDataCustom() + 1);
                break;
        }
    }

    private void autoSave(Tracking tracking) {
        trackingRepository.save(tracking);
    }

    /**
     * GET TRACKING
     */

    private Tracking getTracking(String game, String country, Date timeCreate) {
        String strDate = getStringTimeFormatDay(timeCreate);
        Tracking tracking = trackingRepository.findByDateAndGameAndCountry(strDate, game, country).orElse(null);
        if (tracking == null) {
            tracking = new Tracking();
            tracking.setGame(game);
            tracking.setDate(strDate);
            tracking.setCountry(country);
            tracking.setTimeCreate(timeCreate);
            tracking.setCountDetailLog(new HashMap<>());
            tracking.setNumPartByHour(new HashMap<>());
            trackingRepository.save(tracking);
        }
        if (tracking.getCountDetailLog() == null) {
            tracking.setCountDetailLog(new HashMap<>());
            tracking.setNumPartByHour(new HashMap<>());
        }
        return tracking;
    }

    /**
     * CHECK NUM LOG
     */

    public void checkNumLog(String game, String country) {
        String formatAlert = getAlert(game, country);
        if (formatAlert != null) {
            TelegramController.getInstance().sendInfo(TelegramConst.CHECK_NUM_LOG_SOCKET, game, country, formatAlert);
        }
    }

    public String getAlert(String game, String country) {
        AlertGameInfo config = AlertGameService.getInstance().getConfig(game);
        long now = System.currentTimeMillis();
        ResNumLog resLogNow = getTotalLogNow(game, country, now, config);
        // k co log
        if (resLogNow.getNumLog() == 0) {
            return String.format("Num log game %s in %s = 0", game, getTimeDuration(config.getMinuteCountLog()));
        }
        ResNumLog resLogPast = getTotalLogPast(game, country, now, config);
        // k co log cu de so sanh
        if (resLogPast.getNumLog() == 0 || resLogNow.getMinuteCountLog() != resLogPast.getMinuteCountLog()) {
            return null;
        }
        // so sanh
        long numLogNow = resLogNow.getNumLog();
        long numLogPast = resLogPast.getNumLog();
        if (numLogNow < numLogPast * config.getRateDown()) {
            return String.format("Num log game %s has decreased to %.2f times compared to %s ago", game, 1.0 * numLogNow / numLogPast, getTimeDuration(config.getMinutePastCheck()));
        }
        if (numLogNow > numLogPast * config.getRateUp()) {
            return String.format("Num log game %s has increased to %.2f times compared to %s ago", game, 1.0 * numLogNow / numLogPast, getTimeDuration(config.getMinutePastCheck()));
        }
        return null;
    }

    public ResNumLog getTotalLogNow(String game, String country, long now, AlertGameInfo config) {
        now -= config.getMinuteCountLog() * SECOND_PER_MINUTE * 1000;
        return getTotalLog(game, country, new Date(now));
    }

    public ResNumLog getTotalLogPast(String game, String country, long now, AlertGameInfo config) {
        now -= (config.getMinuteCountLog() + config.getMinutePastCheck()) * SECOND_PER_MINUTE * 1000;
        return getTotalLog(game, country, new Date(now));
    }

    private ResNumLog getTotalLog(String game, String country, Date time) {
        ResNumLog resNumLog = new ResNumLog(0, 0);
        OffsetDateTime offsetDateTime = getOffsetTime(time);
        int hour = offsetDateTime.getHour();
        int minute = offsetDateTime.getMinute();

        String strDate = getStringTimeFormatDay(time);
        Tracking tracking = trackingRepository.findByDateAndGameAndCountry(strDate, game, country).orElse(null);
        if (tracking == null || !tracking.getNumPartByHour().containsKey(hour)) {
            return resNumLog;
        }
        resNumLog.setMinuteCountLog((int) (MINUTE_PER_HOUR / tracking.getNumPartByHour().get(hour)));

        if (tracking.getCountDetailLog() != null && tracking.getCountDetailLog().containsKey(hour)) {
            HashMap<String, long[]> hourLogs = tracking.getCountDetailLog().get(hour);
            long totalLog = 0;
            int idxPart = getIdxPart(tracking, hour, minute);
            for (String action : hourLogs.keySet()) {
                long[] actionLogs = hourLogs.get(action);
                if (actionLogs.length > idxPart) {
                    totalLog += actionLogs[idxPart];
                }
            }
            resNumLog.setNumLog(totalLog);
        }
        return resNumLog;
    }

    /**
     * UTILs
     */

    private OffsetDateTime getOffsetTime(Date timeCreate) {
        return timeCreate.toInstant().atOffset(ZoneOffset.of("+7"));
    }

    private String getStringTimeFormatDay(Date timeCreate) {
        OffsetDateTime offsetDateTime = getOffsetTime(timeCreate);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return fmt.format(offsetDateTime);
    }

    private int getIdxPart(Tracking tracking, int hour, int minute) {
        int minutePart = (int) (MINUTE_PER_HOUR / tracking.getNumPartByHour().get(hour));
        return minute / minutePart;
    }

    public String getTimeDuration(long minute) {
        long day = minute / (MINUTE_PER_HOUR * HOUR_PER_DAY);
        long hour = (minute % (MINUTE_PER_HOUR * HOUR_PER_DAY)) / MINUTE_PER_HOUR;
        long minuteRemain = minute % SECOND_PER_MINUTE;
        return (day > 0 ? day + (day == 1 ? "day " : "days ") : "")
                + (hour > 0 ? hour + (hour == 1 ? "hour " : "hours ") : "")
                + (minuteRemain > 0 ? minuteRemain + (minuteRemain == 1 ? "minute " : "minutes ") : "");
    }

}
