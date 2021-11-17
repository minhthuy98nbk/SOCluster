package com.zingplay.kafka;

import com.google.gson.Gson;
import com.zingplay.log.LogUserAction;
import com.zingplay.models.Item;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.SocketInfo;
import com.zingplay.socket.SocketMessageParser;
import com.zingplay.socket.SocketRequest;
import com.zingplay.socket.v1.response.Offer;
import com.zingplay.socket.v3.response.UserDataCustom;
import com.zingplay.socket.v1.response.UserOffers;
import com.zingplay.service.user.UserService;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;

public class KafkaConsumerController{
    private static final KafkaConsumerController instance = new KafkaConsumerController();

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public static KafkaConsumerController getInstance(){
        return instance;
    }

    public KafkaConsumerController(){
    }

    public void process(String topic, String key, String message) {
        switch (topic){
            case SocketConst.BACK_END_TOPIC:
                //key = "game|country|action" message=string|..|..
                processSocketRequest(key, message);
                break;
            case SocketConst.BACK_END_TOPIC_TEST:
                //key is topic test on client send
                processTestRequest(key, message);
        }
    }

    private void processTestRequest(String key, String message) {
        switch (message){
            case SocketConst.ACTION_CONNECT:
                KafkaSendingWorker.getInstance().send(key,SocketConst.ACTION_CONNECT,"success");
                break;
            case SocketConst.ACTION_OFFER:
                UserOffers userOffer = new UserOffers();
                userOffer.setUserId("12345678");
                userOffer.setVersion("1.0");
                List<Offer> offerList = new ArrayList<>();

                Offer offer = new Offer();
                offer.setId("1");
                offer.setPriority(1);
                offer.setName("OfferTest");
                offer.setPrice(1000);
                offer.setBonus(100);
                offer.setBasePrice(2000);
                offer.setTimeStart(new Date());
                offer.setTimeEnd(new Date());

                Set<Item> itemList = new HashSet<>();
                Item item = new Item();
                item.setId("1");
                item.setValue(1000);
                itemList.add(item);

                Item item1 = new Item();
                item1.setId("gold");
                item1.setValue(1000);
                itemList.add(item1);

                offer.setItems(itemList);

                offerList.add(offer);
                userOffer.setOffers(offerList);
                KafkaSendingWorker.getInstance().send(key,SocketConst.ACTION_OFFER,new Gson().toJson(userOffer));
                break;
            default:
                KafkaSendingWorker.getInstance().send(key,SocketConst.ACTION_OFFER, message);
                break;
        }
    }

    private void processSocketRequest(String key, String message) {
        long t1 = System.currentTimeMillis();
        SocketInfo info = SocketMessageParser.parseKey(key);
        if(info == null) return;

        String game = info.getGame();
        String country = info.getCountry();
        String action = info.getAction();

        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
        SocketRequest socketRequest = SocketMessageParser.parseMessage(info, message, game, country);

        if(socketRequest == null) return;
        socketRequest.execute(info,userService);

        long t2 = System.currentTimeMillis();
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
        LogUserAction.getInstance().info("processBACK_END_TOPIC|" + action +"|" + (t2-t1));
    }

    public void onReceiveAck(ProducerRecord<String, String> producerRecord){
        if(producerRecord == null) return;
        SocketInfo info = SocketMessageParser.parseTopic(producerRecord);
        if(info == null) return;

        switch (info.getAction()){
            case SocketConst.ACTION_OFFER:
                String message = producerRecord.value();
                try {
                    UserOffers userOffer = new Gson().fromJson(message, UserOffers.class);
                    userService.logTrackingUserReceiveOffer(info.getGame(),info.getCountry(),userOffer);
                }catch (Exception e){
                    e.printStackTrace();
                }
            case SocketConst.ACTION_OFFER_LOCAL_PRICE_V2:
                try {
                    com.zingplay.socket.v2.response.UserOffers userOffer = new Gson().fromJson(producerRecord.value(), com.zingplay.socket.v2.response.UserOffers.class);
                    userService.logTrackingUserReceiveOfferV2(info.getGame(),info.getCountry(),userOffer);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case SocketConst.ACTION_GET_DATA_CUSTOM:
                try {
                    UserDataCustom userDataCustom = new Gson().fromJson(producerRecord.value(), UserDataCustom.class);
                    userService.logTrackingUserGetDataCustom(info.getGame(),info.getCountry(), userDataCustom);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
