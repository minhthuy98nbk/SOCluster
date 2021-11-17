package com.zingplay.module.report;

import com.zingplay.helpers.Helpers;
import com.zingplay.models.ReportBuyOffer;
import com.zingplay.module.report.v1.ReportBuyOfferRepository;
import com.zingplay.module.report.v2.ReportOfferDaily;
import com.zingplay.module.report.v2.ReportOfferDailyRepository;
import com.zingplay.module.tracking.ResTracking;
import com.zingplay.module.tracking.Tracking;
import com.zingplay.module.tracking.TrackingRepository;
import com.zingplay.module.tracking.TrackingService;
import com.zingplay.socket.SocketConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    private final ReportBuyOfferRepository reportBuyOfferRepository;
    private final ReportOfferDailyRepository reportOfferDailyRepository;
    private final TrackingRepository trackingRepository;
    private final TrackingService trackingService;

    private final Helpers helpers;
    private static String[] actionLogName;

    static {
        actionLogName = new String[] {SocketConst.ACTION_CONNECT,
                SocketConst.ACTION_OFFER,
                SocketConst.ACTION_LOGIN,
                SocketConst.ACTION_TRACKING,
                SocketConst.ACTION_STATS_GAME,
                SocketConst.ACTION_USER_PAYMENT,
                SocketConst.ACTION_USER_BUY_OFFER,
                SocketConst.ACTION_USER_REQUEST_OFFERS,
                SocketConst.ACTION_GET_DATA_CUSTOM,
                SocketConst.ACTION_RECEIVE_DATA_CUSTOM,
                SocketConst.ACTION_USER_REQUEST_OFFERS_V2,
                SocketConst.ACTION_GET_OFFER,
                SocketConst.ACTION_USER_BUY_OFFER_V2,
                SocketConst.ACTION_OFFER_LOCAL_PRICE_V2};
    }
    @Autowired
    public ReportService(ReportBuyOfferRepository reportBuyOfferRepository, ReportOfferDailyRepository reportOfferDailyRepository, TrackingRepository trackingRepository, TrackingService trackingService, Helpers helpers) {
        this.reportBuyOfferRepository = reportBuyOfferRepository;
        this.reportOfferDailyRepository = reportOfferDailyRepository;
        this.trackingRepository = trackingRepository;
        this.trackingService = trackingService;
        this.helpers = helpers;
    }


    public Object getReportRunOffer(long from, long to) {
        Date date = new Date(from);
        Date date1 = new Date(to);
        String game = helpers.getGame();
        String country = helpers.getCountry();
        List<ReportBuyOffer> allByGameAndCountryAndTimeCreateBetween = reportBuyOfferRepository.findAllByGameAndCountryAndTimeCreateBetween(game, country, date, date1);
        return allByGameAndCountryAndTimeCreateBetween;
    }
    public Object getReportDailyRunOffer(long from, long to) {
        Date date = new Date(from);
        Date date1 = new Date(to);
        String game = helpers.getGame();
        String country = helpers.getCountry();
        List<ReportOfferDaily> allByGameAndCountryAndTimeCreateBetween = reportOfferDailyRepository.findAllByGameAndCountryAndTimeCreateBetween(game, country, date, date1);
        return allByGameAndCountryAndTimeCreateBetween;
    }

    public Object getReportTrackingRequest(long from, long to) {
        Date date = new Date(from);
        Date date1 = new Date(to);
        String game = helpers.getGame();
        String country = helpers.getCountry();
        List<Tracking> allByGameAndCountryAndTimeCreateBetween = trackingRepository.findAllByGameAndCountryAndTimeCreateBetween(game, country, date, date1);
        return allByGameAndCountryAndTimeCreateBetween;
    }

    public Object getReportTrackingDetail(long from, long to, boolean isRaw) {
        Date date = new Date(from);
        Date date1 = new Date(to);
        String game = helpers.getGame();
        String country = helpers.getCountry();
        List<Tracking> trackings = trackingRepository.findAllByGameAndCountryAndTimeCreateBetween(game, country, date, date1);
        return isRaw ? getResTrackingRaw(trackings) : getResTrackingHour(trackings);
    }

    public List<ResTracking> getResTrackingHour(List<Tracking> trackings){
        List<ResTracking> result = new ArrayList<>();
        for (Tracking tracking : trackings){
            HashMap<Integer, HashMap<String, long[]>> hourLogs = tracking.getCountDetailLog();
            for (int i = 0; i < trackingService.HOUR_PER_DAY; i++){
                if (hourLogs.get(i) == null){
                    result.add(new ResTracking(tracking, i + "h " + tracking.getDate(), new HashMap<>()));
                } else {
                    result.add(new ResTracking(tracking, i + "h " + tracking.getDate(), this.getCountLog(TypeTimeReport.HOUR, hourLogs.get(i), -1)));
                }
            }
        }
        return result;
    }

    public List<ResTracking> getResTrackingRaw(List<Tracking> trackings){
        List<ResTracking> result = new ArrayList<>();
        for (Tracking tracking : trackings){
            HashMap<Integer, Integer> numPartMap = tracking.getNumPartByHour();
            HashMap<Integer, HashMap<String, long[]>> hourLogs = tracking.getCountDetailLog();
            for (int i = 0; i < trackingService.HOUR_PER_DAY; i++){
                if (hourLogs.get(i) == null){
                    result.add(new ResTracking(tracking, i + "h " + tracking.getDate(), new HashMap<>()));
                } else {
                    int numPart = numPartMap.get(i);
                    for (int j = 0; j < numPart; j++){
                        String time = i + "h" + (j*trackingService.MINUTE_PER_HOUR/numPart) + "m " + tracking.getDate();
                        result.add(new ResTracking(tracking, time, getCountLog(TypeTimeReport.RAW, hourLogs.get(i), j)));
                    }
                }
            }
        }
        return result;
    }

    public long getNumLogRaw(HashMap<String, long[]> actionLogs, String action, int idxPart){
        if (actionLogs != null && actionLogs.containsKey(action) && actionLogs.get(action).length > idxPart){
            return actionLogs.get(action)[idxPart];
        }
        return 0;
    }

    public long getNumLogHour(HashMap<String, long[]> actionLogs, String action){
        if (actionLogs != null && actionLogs.containsKey(action)){
            return Arrays.stream(actionLogs.get(action)).sum();
        }
        return 0;
    }

    public HashMap<String, Long> getCountLog(TypeTimeReport typeTimeReport, HashMap<String, long[]> actionLogs, int idxPart){
        HashMap<String, Long> countLog = new HashMap<>();
        for (String action: actionLogName){
            long count = (typeTimeReport == TypeTimeReport.RAW)
                    ? this.getNumLogRaw(actionLogs, action, idxPart)
                    : this.getNumLogHour(actionLogs, action);
            if (count > 0) {
                countLog.put(action, count);
            }
        }
        return countLog;
    }
}
