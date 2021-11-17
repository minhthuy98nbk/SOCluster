package com.zingplay.kafka;

import com.zingplay.helpers.GsonHelper;
import com.zingplay.socket.v1.request.*;
import com.zingplay.socket.v2.request.UserRequestOfferDetail;
import com.zingplay.socket.v2.request.UserRequestOfferV2;
import com.zingplay.socket.v2.request.UserTrackingBuyOfferV2;
import com.zingplay.socket.v3.request.UserReceiveDataCustom;
import com.zingplay.socket.v3.request.UserRequestDataCustom;
import com.zingplay.socket.v3.UserTrackingCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConverter {
    private final static String VERSION="1";
    private final static String SEPARATOR_CHAR = "\\|";
    private static final Logger log = LoggerFactory.getLogger(KafkaConverter.class);
    public static UserTrackingLogin toUserTrackingLogin(String[] split){
        if(split.length >= 7){
            UserTrackingLogin userTracking = new UserTrackingLogin();
            // String version = split[0];
            try {
                userTracking.setUserId(split[1]);
                userTracking.setTimeCreate(getLongTime(split[2]));
                userTracking.setTimeOnline(getLongTime(split[3]));
                userTracking.setChannelIdx(Integer.parseInt(split[4]));
                userTracking.setTotalGame(Long.parseLong(split[5]));
                userTracking.setTimeCurrent(getLongTime(split[6]));
                return userTracking;
            }catch (NumberFormatException e){
                log.error("fail convert number to UserTrackingLogin|" + String.join("|", split));
            }
        }else{
            log.error("fail convert to UserTrackingLogin|" + String.join("|", split));
        }
        return null;
    }
    public static long getLongTime(String s){
        try {
            long l = Long.parseLong(s);
            if(l < 99999999999L){
                return l * 1000;
            }
            return l;
        }catch (NumberFormatException e){
            throw e;
        }
    }

    public static UserTrackingStateGame toUserTrackingStateGame(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 5){
            UserTrackingStateGame trackingStateGame = new UserTrackingStateGame();
            // String version = split[0];
            try {
                trackingStateGame.setUserId(split[1]);
                trackingStateGame.setTotalGame(Long.parseLong(split[2]));
                trackingStateGame.setChannelIdx(Integer.parseInt(split[3]));
                trackingStateGame.setTimeCurrent(getLongTime(split[4]));
                return trackingStateGame;
            }catch (NumberFormatException e){
                log.error("fail convert number to UserTrackingStateGame|" + msg);
            }

        }else{
            log.error("fail convert to UserTrackingStateGame|" + msg);
        }
        return null;
    }
    public static UserTrackingPayment toUserTrackingPayment(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 5){
            UserTrackingPayment tracking = new UserTrackingPayment();
            // String version = split[0];
            try {
                tracking.setUserId(split[1]);
                tracking.setPackCost(Float.parseFloat(split[2]));
                tracking.setChannelPayment(split[3]);
                tracking.setTimeCurrent(getLongTime(split[4]));
                return tracking;
            }catch (NumberFormatException e){
                log.error("fail convert number to toUserTrackingPayment|" + msg);
            }

        }else{
            log.error("fail convert to toUserTrackingPayment|" + msg);
        }
        return null;
    }
    public static UserTrackingPayment toUserTrackingPaymentV2(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 5){
            UserTrackingPayment tracking = new UserTrackingPayment();
            // String version = split[0];
            try {
                tracking.setUserId(split[1]);
                tracking.setPackCost(Float.parseFloat(split[2]));
                tracking.setChannelPayment(split[3]);
                tracking.setCountry(split[4]);
                tracking.setTimeCurrent(getLongTime(split[5]));
                return tracking;
            }catch (NumberFormatException e){
                log.error("fail convert number to toUserTrackingPayment|" + msg);
            }

        }else{
            log.error("fail convert to toUserTrackingPayment|" + msg);
        }
        return null;
    }
    public static UserTrackingBuyOffer toUserTrackingBuyOffer(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 4){
            UserTrackingBuyOffer tracking = new UserTrackingBuyOffer();
            // String version = split[0];
            try {
                tracking.setUserId(split[1]);
                tracking.setIdOffer(split[2]);
                tracking.setTimeCurrent(getLongTime(split[3]));
                return tracking;
            }catch (NumberFormatException e){
                log.error("fail convert number to UserTrackingBuyOffer|" + msg);
            }

        }else{
            log.error("fail convert to UserTrackingBuyOffer|" + msg);
        }
        return null;
    }

    /**
     * co tra them quoc gia mua offer đó
     * @param msg
     * @return
     */
    public static UserTrackingBuyOfferV2 toUserTrackingBuyOfferV2(String msg){
        //version|userId|idOffer|country|timeBuy
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 4){
            UserTrackingBuyOfferV2 tracking = new UserTrackingBuyOfferV2();
            // String version = split[0];
            try {
                tracking.setUserId(split[1]);
                tracking.setIdOffer(split[2]);
                tracking.setCountry(split[3]);
                tracking.setPrice(Float.parseFloat(split[4]));
                tracking.setTimeCurrent(getLongTime(split[5]));
                return tracking;
            }catch (NumberFormatException e){
                log.error("fail convert number to UserTrackingBuyOffer|" + msg);
            }

        }else{
            log.error("fail convert to UserTrackingBuyOffer|" + msg);
        }
        return null;
    }
    public static UserRequestOffer toUserRequestOffer(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 3){
            UserRequestOffer tracking = new UserRequestOffer();
            // String version = split[0];
            tracking.setUserId(split[1]);
            tracking.setCurTime(getLongTime(split[2]));
            return tracking;
        }else{
            log.error("fail convert to UserRequestOffer|" + msg);
        }
        return null;
    }

    public static UserRequestDataCustom toUserRequestDataCustom(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 3){
            UserRequestDataCustom tracking = new UserRequestDataCustom();
            // String version = split[0];
            tracking.setUserId(split[1]);
            tracking.setCurTime(getLongTime(split[2]));
            return tracking;
        }else{
            log.error("fail convert to UserRequestOffer|" + msg);
        }
        return null;
    }

    public static UserReceiveDataCustom toUserHandleDataCustom(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 3){
            UserReceiveDataCustom tracking = new UserReceiveDataCustom();
            // String version = split[0];
            tracking.setUserId(split[1]);
            tracking.setTypeCustom(split[2]);
            tracking.setDataId(split[3]);
            tracking.setTimeCurrent(getLongTime(split[4]));
            tracking.setCountry(split[5]);
            return tracking;
        }else{
            log.error("fail convert to UserHandleDataCustom|" + msg);
        }
        return null;
    }

    public static UserRequestOfferV2 toUserRequestOfferV2(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 4){
            UserRequestOfferV2 tracking = new UserRequestOfferV2();
            // String version = split[0];
            tracking.setUserId(split[1]);
            tracking.setCurTime(getLongTime(split[2]));
            tracking.setCountry(split[3]);
            return tracking;
        }else{
            log.error("fail convert to UserRequestOffer|" + msg);
        }
        return null;
    }
    public static UserRequestOfferDetail toUserRequestOfferDetail(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 2){
            UserRequestOfferDetail tracking = new UserRequestOfferDetail();
            // String version = split[0];
            tracking.setIdOffer(split[1]);
            if(split.length >=3){
                tracking.setCountry(split[2]);
            }
            return tracking;
        }else{
            log.error("fail convert to UserRequestOfferDetail|" + msg);
        }
        return null;
    }
    public static UserTrackingCustom toUserTrackingCustom(String msg){
        String[] split = msg.split(SEPARATOR_CHAR);
        if(split.length >= 2){
            UserTrackingCustom tracking = new UserTrackingCustom();
            // String version = split[0];
            tracking.setUserId(split[1]);
            tracking.setTimeCreate(Long.parseLong(split[2]));
            tracking.setTimeCurrent(Long.parseLong(split[3]));

            if (split.length >= 5) {
                tracking.setTrackingStr(GsonHelper.parseString(split[4]));
            }
            if (split.length >= 6) {
                tracking.setTrackingLong(GsonHelper.parseLong(split[5]));
            }
            if (split.length >= 7) {
                tracking.setTrackingFloat(GsonHelper.parseFloat(split[6]));
            }
            if (split.length >= 8) {
                tracking.setTrackingObject(GsonHelper.parseObject(split[7]));
            }
            if (split.length >= 9) {
                tracking.setTrackingDuration(GsonHelper.parseLong(split[8]));
            }

            return tracking;
        }else{
            log.error("fail convert to UserRequestOfferDetail|" + msg);
        }
        return null;
    }
}
