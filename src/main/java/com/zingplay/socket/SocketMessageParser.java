package com.zingplay.socket;

import com.google.gson.Gson;
import com.zingplay.cron.CronJobServiceImpl;
import com.zingplay.kafka.KafkaConverter;
import com.zingplay.socket.v1.response.UserOffers;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SocketMessageParser {

    public static SocketInfo parseKey(String key){
        if(key == null) return null;
        if(key.trim().isEmpty()) return null;

        String[] split = key.split(SocketConst.SEPARATOR);
        if(split.length < 3) return null;

        String game = split[0];
        String country = split[1];
        String action = split[2];

        SocketInfo info = new SocketInfo();
        info.setGame(game);
        info.setCountry(country);
        info.setAction(action);
        return info;
    }

    public static SocketRequest parseMessage(SocketInfo info, String message, String game, String country) {
        if(info == null) return null;
        if(message == null) return null;
        if(message.trim().isEmpty()) return null;

        String action = info.getAction();
        if(action == null) return null;
        String[] split = message.split(SocketConst.SEPARATOR);
        if(split.length <= 1) return null;
        String version = split[0];
        CronJobServiceImpl.getInstance().addGameVersion(game, version);
        switch (version){
            case SocketConst.VERSION_1:
                switch (action){
                    case SocketConst.ACTION_LOGIN:
                        return KafkaConverter.toUserTrackingLogin(split);
                    case SocketConst.ACTION_STATS_GAME:
                        return KafkaConverter.toUserTrackingStateGame(message);
                    case SocketConst.ACTION_USER_PAYMENT:
                        return KafkaConverter.toUserTrackingPayment(message);
                    case SocketConst.ACTION_USER_BUY_OFFER:
                        return KafkaConverter.toUserTrackingBuyOffer(message);
                    case SocketConst.ACTION_USER_REQUEST_OFFERS:
                        return KafkaConverter.toUserRequestOffer(message);
                }
            case SocketConst.VERSION_2:
                switch (action){
                    case SocketConst.ACTION_LOGIN:
                        return KafkaConverter.toUserTrackingLogin(split);
                    case SocketConst.ACTION_STATS_GAME:
                        return KafkaConverter.toUserTrackingStateGame(message);
                    case SocketConst.ACTION_USER_PAYMENT:
                        return KafkaConverter.toUserTrackingPaymentV2(message);
                    case SocketConst.ACTION_USER_BUY_OFFER_V2:
                        return KafkaConverter.toUserTrackingBuyOfferV2(message);
                    case SocketConst.ACTION_USER_REQUEST_OFFERS_V2:
                        return KafkaConverter.toUserRequestOfferV2(message);
                    case SocketConst.ACTION_GET_OFFER:
                        return KafkaConverter.toUserRequestOfferDetail(message);
                }
            case SocketConst.VERSION_3:
                switch (action){
                    case SocketConst.ACTION_LOGIN:
                        return KafkaConverter.toUserTrackingLogin(split);
                    case SocketConst.ACTION_STATS_GAME:
                        return KafkaConverter.toUserTrackingStateGame(message);
                    case SocketConst.ACTION_USER_PAYMENT:
                        return KafkaConverter.toUserTrackingPaymentV2(message);
                    case SocketConst.ACTION_USER_BUY_OFFER_V2:
                        return KafkaConverter.toUserTrackingBuyOfferV2(message);
                    case SocketConst.ACTION_USER_REQUEST_OFFERS_V2:
                        return KafkaConverter.toUserRequestOfferV2(message);
                    case SocketConst.ACTION_GET_OFFER:
                        return KafkaConverter.toUserRequestOfferDetail(message);
                    case SocketConst.ACTION_TRACKING:
                        return KafkaConverter.toUserTrackingCustom(message);
                    case SocketConst.ACTION_GET_DATA_CUSTOM:
                        return KafkaConverter.toUserRequestDataCustom(message);
                    case SocketConst.ACTION_RECEIVE_DATA_CUSTOM:
                        return KafkaConverter.toUserHandleDataCustom(message);
                }
        }
        return null;
    }

    public static SocketInfo parseTopic(ProducerRecord<String, String> producerRecord) {
        String topic = producerRecord.topic();
        if(topic.startsWith(SocketConst.PREFIX_KEY)){
            String[] split = topic.split(SocketConst.SEPARATOR_TOPIC);
            int length = split.length;
            if(length >= 3){
                String key = producerRecord.key();
                SocketInfo info = new SocketInfo();
                String country = split[length-1];
                String game = split[length-2];
                info.setGame(game);
                info.setCountry(country);
                info.setAction(key);
                return info;
            }
        }
        return null;
    }
}
