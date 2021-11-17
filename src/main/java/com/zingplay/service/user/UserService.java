package com.zingplay.service.user;

import com.zingplay.beans.UserTracking;
import com.zingplay.helpers.Helpers;
import com.zingplay.helpers.TrackingHelpers;
import com.zingplay.log.*;
import com.zingplay.models.Object;
import com.zingplay.models.*;
import com.zingplay.module.objects.ConditionController;
import com.zingplay.module.report.v1.ReportBuyOfferRepository;
import com.zingplay.module.report.v2.ReportOfferService;
import com.zingplay.module.telegram.TelegramConst;
import com.zingplay.module.telegram.TelegramController;
import com.zingplay.module.tracking.TrackingService;
import com.zingplay.repository.*;
import com.zingplay.socket.SocketConst;
import com.zingplay.socket.v1.request.*;
import com.zingplay.socket.v3.TrackingCommon;
import com.zingplay.socket.v3.request.UserReceiveDataCustom;
import com.zingplay.socket.v3.response.DataCustom;
import com.zingplay.socket.v1.response.Offer;
import com.zingplay.socket.v3.response.TypeCustom;
import com.zingplay.socket.v3.response.UserDataCustom;
import com.zingplay.socket.v1.response.UserOffers;
import com.zingplay.socket.v2.request.UserTrackingBuyOfferV2;
import com.zingplay.socket.v2.response.Price;
import com.zingplay.socket.v3.UserTrackingCustom;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserObjectRepository userObjectRepository;
    private final UserBuyOfferRepository userBuyOfferRepository;
    private final RunOfferRepository runOfferRepository;
    private final CustomGiftRepository customGiftRepository;
    private final UserReceivedRepository userReceivedRepository;
    private final ReportBuyOfferRepository reportBuyOfferRepository;
    private final ReportOfferService reportOfferService;
    private final TrackingService trackingService;
    private final CustomDataService customDataService;
    private Helpers helpers;

    @Autowired
    public UserService(UserRepository userRepository, UserObjectRepository userObjectRepository, @Lazy UserBuyOfferRepository userBuyOfferRepository, RunOfferRepository runOfferRepository, CustomGiftRepository customGiftRepository, UserReceivedRepository userReceivedRepository, ReportBuyOfferRepository reportBuyOfferRepository, ReportOfferService reportOfferService, TrackingService trackingService, CustomDataService customDataService, Helpers helpers) {
        this.userRepository = userRepository;
        this.userObjectRepository = userObjectRepository;
        this.userBuyOfferRepository = userBuyOfferRepository;
        this.runOfferRepository = runOfferRepository;
        this.customGiftRepository = customGiftRepository;
        this.userReceivedRepository = userReceivedRepository;
        this.reportBuyOfferRepository = reportBuyOfferRepository;
        this.reportOfferService = reportOfferService;
        this.trackingService = trackingService;
        this.customDataService = customDataService;
        this.helpers = helpers;
    }
    public void logTracking(UserTrackingCustom tracking, String game, String country){
        if(tracking == null) {
            LogKafka.getInstance().error("logTracking tracking null",game,country);
            return;
        }
        Integer userId = Integer.parseInt(tracking.getUserId());
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId,game,country).orElse(null);
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        if(user == null){
            LogKafka.getInstance().info("logTrackingLogin user not found, create new|{}|{}|{}",game,country, userId);
            user = new User();
            user.setTimeCreate(tracking.getTimeCreate());
            user.setUserId(userId);
        }
        // user.setGame(game);
        // user.setCountry(country);
        //=> convert giá trị cũ -> giá trị mới -> bỏ giá trị cũ
        user.setTimeCreate(tracking.getTimeCreate());
        //CHECK VALID TẤT CẢ CÁC KEy
        //convert data cũ sang data mới -> không cân -> lúc quét check là được

        boolean validAllKey = ConditionController.getInstance().isValidAllKey(game, tracking);
        if(validAllKey){
            ConditionController.getInstance().updateData(game,user,tracking);
        }else{
            //
            //noti tele sai data ghep noi
            TelegramController.getInstance().sendWarning(TelegramConst.TRACKING_DATA_WRONG,game,country,tracking.toString());
        }
        // user.setChannelGame(tracking.getChannelIdx());
        // user.setTotalGame(tracking.getTotalGame());

        // tracking custom van lay time online de check remove
         user.setTimeOnline(new Date());
        // todo remove hard code cheat to test remove
        // user.setTimeOnline(new Date(user.getTrackingLong("timeOnline")));

        userRepository.save(user);
        String databaseNameForCurrentThread = MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread();
        String check = game + "_" + country;
        if(!databaseNameForCurrentThread.endsWith(check)){
            LogSystemAction.getInstance().error("Failed multi db |" + databaseNameForCurrentThread +"|" + check);
        }
        LogKafka.getInstance().info("logTrackingLogin updated|{}|{}|{}|{}|{}|{}",game,country, userId, tracking);

        trackingService.incrementTracking(game, country, tracking.getTimeCurrentLong(), SocketConst.ACTION_LOGIN);
    }

    public void logTrackingRegister(UserTrackingLogin tracking, String game, String country){
        if(tracking == null) {
            LogKafka.getInstance().error("logTrackingLogin tracking null",game,country);
            return;
        }
        Integer userId = Integer.parseInt(tracking.getUserId());
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId,game,country).orElse(null);
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        if(user == null){
            LogKafka.getInstance().info("logTrackingLogin user not found, create new|{}|{}|{}",game,country, userId);
            user = new User();
            user.setTimeCreate(new Date(tracking.getTimeCreate()));
            user.setGame(game);
            user.setCountry(country);
            user.setUserId(userId);
        }
        user.setGame(game);
        user.setCountry(country);
        user.setChannelGame(tracking.getChannelIdx());
        user.setTotalGame(tracking.getTotalGame());
        user.setTimeOnline(new Date(tracking.getTimeCurrent()));
        user.setTimeCreate(new Date(tracking.getTimeCreate()));

        userRepository.save(user);
        String databaseNameForCurrentThread = MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread();
        String check = game + "_" + country;
        if(!databaseNameForCurrentThread.endsWith(check)){
            LogSystemAction.getInstance().error("Failed multi db |" + databaseNameForCurrentThread +"|" + check);
        }
        LogKafka.getInstance().info("logTrackingLogin updated|{}|{}|{}|{}|{}|{}",game,country, userId, tracking.getTotalGame(), tracking.getChannelIdx(), tracking.getTimeCurrent());
        trackingService.incrementTracking(game, country,tracking.getTimeCurrent(), SocketConst.ACTION_LOGIN);
    }

    public void logTrackingLogin(UserTrackingLogin tracking, String game, String country){
        if(tracking == null) {
            LogKafka.getInstance().error("logTrackingLogin tracking null",game,country);
            return;
        }
        Integer userId = Integer.parseInt(tracking.getUserId());
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId,game,country).orElse(null);
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        if(user == null){
            LogKafka.getInstance().info("logTrackingLogin user not found, create new|{}|{}|{}",game,country, userId);
            user = new User();
            user.setTimeCreate(new Date(tracking.getTimeCreate()));
            user.setGame(game);
            user.setCountry(country);
            user.setUserId(userId);
        }
        user.setGame(game);
        user.setCountry(country);
        user.setChannelGame(tracking.getChannelIdx());
        user.setTotalGame(tracking.getTotalGame());
        user.setTimeOnline(new Date(tracking.getTimeCurrent()));
        user.setTimeCreate(new Date(tracking.getTimeCreate()));

        userRepository.save(user);
        String databaseNameForCurrentThread = MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread();
        String check = game + "_" + country;
        if(!databaseNameForCurrentThread.endsWith(check)){
            LogSystemAction.getInstance().error("Failed multi db |" + databaseNameForCurrentThread +"|" + check);
        }
        LogKafka.getInstance().info("logTrackingLogin updated|{}|{}|{}|{}|{}|{}",game,country, userId, tracking.getTotalGame(), tracking.getChannelIdx(), tracking.getTimeCurrent());
        trackingService.incrementTracking(game, country,tracking.getTimeCurrent(), SocketConst.ACTION_LOGIN);
    }

    public void logTrackingStateGame(UserTrackingStateGame tracking, String game, String country){
        if(tracking == null){
            LogKafka.getInstance().error("logTrackingStateGame tracking null|{}|{}",game,country);
            return;
        }

        Integer userId = Integer.parseInt(tracking.getUserId());
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId,game,country).orElse(null);
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        if(user == null){
            LogKafka.getInstance().error("logTrackingStateGame user not found|{}|{}|{}",game,country, tracking.getUserId());
            //error not found
            return;
        }
        user.setChannelGame(tracking.getChannelIdx());
        user.setTotalGame(tracking.getTotalGame());
        user.setTimeOnline(new Date(tracking.getTimeCurrent()));
        userRepository.save(user);
        LogKafka.getInstance().info("logTrackingStateGame updated|{}|{}|{}|{}|{}",game,country, tracking.getUserId(), tracking.getChannelIdx(), tracking.getTotalGame());
        trackingService.incrementTracking(game, country,tracking.getTimeCurrent(), SocketConst.ACTION_STATS_GAME);
    }

    public void logTrackingPayment(UserTrackingPayment tracking, String game, String country){
        if(tracking == null) {
            LogKafka.getInstance().error("logTrackingPayment tracking null|{}|{}",game,country);
            return;
        }
        Integer userId = Integer.parseInt(tracking.getUserId());
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId,game,country).orElse(null);
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        //User user = userRepository.findById(tracking.getIdUser()).orElse(null);
        if(user == null){
            //error not found
            LogKafka.getInstance().error("logTrackingPayment user not found|{}|{}|{}",game,country, tracking.getUserId());
            return;
        }
        //todo chuyển về đúng mệnh giá local
        float totalPaid = user.getTotalPaid() + tracking.getPackCost();
        user.setTotalPaid(totalPaid);
        int totalTimesPaid = user.getTotalTimesPaid() + 1;
        user.setTotalTimesPaid(totalTimesPaid);
        String lastPaidPack = tracking.getPackCost() + "";
        user.setLastPaidPack(lastPaidPack);
        user.addChannelPayment(tracking.getChannelPayment());
        user.setTimeOnline(new Date(tracking.getTimeCurrent()));
        user.setTracking(TrackingCommon.TotalPaid,totalPaid);
        user.setTracking(TrackingCommon.TotalTimesPaid,totalTimesPaid);
        user.setTracking(TrackingCommon.LastPaidPack,tracking.getPackCost());
        userRepository.save(user);
        trackingService.incrementTracking(game, country, tracking.getTimeCurrent(), SocketConst.ACTION_USER_PAYMENT);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void logTrackingBuyOffer(UserTrackingBuyOffer tracking, String game, String country){
        if(tracking == null) {
            LogKafka.getInstance().error("logTrackingBuyOffer tracking null|{}|{}",game,country);
            return;
        }

        Integer userId = Integer.parseInt(tracking.getUserId());
        User user = userRepository.findFirstByUserId(userId).orElse(null);
        if(user == null){
            //error not found
            LogKafka.getInstance().error("logTrackingBuyOffer user not found|{}|{}|{}",game,country, tracking.getUserId());
            return;
        }

        String idOffer = tracking.getIdOffer();
        long timeCurrent = tracking.getTimeCurrent();
        userBuyOffer(user, idOffer, timeCurrent,country);
        trackingService.incrementTracking(game, country,tracking.getTimeCurrent(), SocketConst.ACTION_USER_BUY_OFFER);
    }
    public UserBuyOffer createUserBuyOffer(UserTrackingFakeBuyOffer tracking) {
        if(tracking == null) {
            LogGameDesignAction.getInstance().error("createUserBuyOffer tracking null");
            return null;
        }

        String idUser = tracking.getIdUser();
        User user = userRepository.findById(idUser).orElse(null);
        if(user == null){
            //error not found
            LogGameDesignAction.getInstance().error("createUserBuyOffer user not found|{}", idUser);
            return null;
        }

        String idOffer = tracking.getIdOffer();
        long timeCurrent = tracking.getTimeCurrent();
        LogGameDesignAction.getInstance().info("createUserBuyOffer|{}|{}|{}", helpers.getUsername(), idUser, idOffer);
        return userBuyOffer(user, idOffer, timeCurrent, user.getCountry());
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public UserBuyOffer userBuyOffer(User user, String id_offer, long timeCurrent, String countryBuy){
        return reportOfferService.userBuyOffer(user,id_offer,timeCurrent,countryBuy,0);
        /*//todo log report theo country
        LogUserAction.getInstance().info("userBuyOffer|{}|{}|{}", user.getUserId(), id_offer, timeCurrent);
        RunOffer runOffer = runOfferRepository.findById(id_offer).orElse(null);
        if(runOffer == null){
            //not found ->
            LogUserAction.getInstance().error("userBuyOffer userRunOffer null|{}|{}|{}", user.getId(), id_offer, timeCurrent);
        }else{
            Date timeCreate = new Date(timeCurrent);
            OffsetDateTime offsetDateTime = timeCreate.toInstant().atOffset(ZoneOffset.of("+7"));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String strDate = fmt.format(offsetDateTime);
            String game = user.getGame();
            String country = user.getCountry();
            com.zingplay.models.Offer offer = runOffer.getOffer();
            LogUserAction.getInstance().info("userBuyOffer|{}|{}|{}|{}", user.getUserId(), offer.getIdOffer(), offer.getPrice(), timeCurrent);
            float price = offer.getPrice();
            runOffer.setCountUser(runOffer.getCountUser() + 1);
            runOffer.setTotalRev(runOffer.getTotalRev() + price);

            UserBuyOffer userBuyOffer = new UserBuyOffer();
            userBuyOffer.setAmount(price);
            userBuyOffer.setUser(user);
            userBuyOffer.setRunOffer(runOffer);
            userBuyOffer.setTimeCreate(timeCreate);

            ReportBuyOffer reportBuyOffer = reportBuyOfferRepository.findByDateAndGameAndCountry(strDate, game, country).orElseGet(() -> {
                ReportBuyOffer r = new ReportBuyOffer();
                r.setGame(game);
                r.setCountry(country);
                r.setDate(strDate);
                r.setTimeCreate(timeCreate);
                return r;
            });
            reportBuyOffer.setTotalUser(reportBuyOffer.getTotalUser() + 1);
            reportBuyOffer.setTotalRev(reportBuyOffer.getTotalRev() + price);

            runOfferRepository.save(runOffer);
            reportBuyOfferRepository.save(reportBuyOffer);
            return userBuyOfferRepository.save(userBuyOffer);

           }
           return null;
           */

    };

    public UserOffers getOffers(String userId, String game, String country, long curTime){
        long t1 = System.currentTimeMillis();
        User user = userRepository.findFirstByUserId(Integer.parseInt(userId)).orElse(null);
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(Integer.parseInt(userId),game,country).orElse(null);
        long t2 = System.currentTimeMillis();
        UserOffers response = new UserOffers();
        response.setUserId(userId);

        ArrayList<Offer> offers = new ArrayList<>();
        //User user = userRepository.findById(userId).orElse(null);
        //test get truc tiep
        if(user != null){
            Date curDate = new Date(curTime);
            Stream<UserObject> userObjectStream = userObjectRepository.streamAllByUser(user);
            long t3 = System.currentTimeMillis();
            userObjectStream.forEach(userObject -> {
                Object object = userObject.getObject();
                if (object != null) {
                    Stream<RunOffer> runOfferStream = runOfferRepository.streamAllByObjectAndTimeEndAfter(object,curDate);
                    runOfferStream.forEach(runOffer -> {
                        Offer offerResponse = new Offer();
                        //Date timeEnd = runOffer.getTimeEnd();
                        //if(curDate.before(timeEnd))
                        {
                            com.zingplay.models.Offer offer = runOffer.getOffer();
                            if (offer != null) {
                                offerResponse.setId(runOffer.getId());
                                offerResponse.setPriority(runOffer.getPriority());
                                offerResponse.setIdRunOffer(runOffer.getIdRunOffer());
                                offerResponse.setCountry(offer.getRegion());
                                offerResponse.setName(offer.getDisplayName());
                                offerResponse.setPrice(offer.getPrice());
                                offerResponse.setItems(offer.getItems());
                                offerResponse.setBasePrice(offer.getBasePrice());
                                offerResponse.setBonus(offer.getBonus());
                                offerResponse.setIconNum(offer.getIconNum());
                                offerResponse.setThemeNum(offer.getThemeNum());
                                offerResponse.setTimeStart(runOffer.getTimeStart());
                                offerResponse.setTimeEnd(runOffer.getTimeEnd());
                                offers.add(offerResponse);
                                //}else{
                                //    runOfferRepository.delete(runOffer);
                            }
                        }
                    });
                }
            });
            long t4 = System.currentTimeMillis();
            LogUserAction.getInstance().info("getOffers test|" + offers.size() +  game +"|" +  country +"|" +  userId  +"|" + (t4-t3) +"|"+ (t3-t2) +"|"+ (t2-t1) +"|"+ (t4-t1) +"|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
        }
        //old
        //if(user != null){
        //    //PageRequest pageRequest = PageRequest.of(0,10,Sort.by("timeCreate").descending());
        //    UserRunOffer userRunOfferList = userRunOfferRepository.findOneByUser_Id(user.getId()).orElse(null);
        //    long t3 = System.currentTimeMillis();
        //    Date curDate = new Date(curTime);
        //    if(userRunOfferList != null){
        //        Set<RunOffer> runOffers = userRunOfferList.getRunOffers();
        //        for (RunOffer runOffer : runOffers) {
        //            Date timeEnd = runOffer.getTimeEnd();
        //            if(curDate.before(timeEnd)){
        //                com.zingplay.models.Offer offer = runOffer.getOffer();
        //                if(offer == null){continue;}
        //                Offer offerResponse = new Offer();
        //                offerResponse.setId(runOffer.getId());
        //                offerResponse.setPriority(runOffer.getPriority());
        //                offerResponse.setIdRunOffer(runOffer.getIdRunOffer());
        //                offerResponse.setCountry(offer.getRegion());
        //                offerResponse.setName(offer.getDisplayName());
        //                offerResponse.setPrice(offer.getPrice());
        //                offerResponse.setItems(offer.getItems());
        //                offerResponse.setBasePrice(offer.getBasePrice());
        //                offerResponse.setBonus(offer.getBonus());
        //                offerResponse.setIconNum(offer.getIconNum());
        //                offerResponse.setThemeNum(offer.getThemeNum());
        //                offerResponse.setTimeStart(runOffer.getTimeStart());
        //                offerResponse.setTimeEnd(runOffer.getTimeEnd());
        //                offers.add(offerResponse);
        //            }
        //        }
        //        long t4 = System.currentTimeMillis();
        //        LogUserAction.getInstance().info("getOffers|time|" +  game +"|" +  country +"|" +  userId  +"|" + (t4-t3) +"|"+ (t3-t2) +"|"+ (t2-t1) +"|"+ (t4-t1));
        //    }else{
        //        LogUserAction.getInstance().info("getOffers chua co offer cho user|{}|{}|{}", game, country, userId);
        //    }
        //}else{
        //    LogUserAction.getInstance().error("getOffers user null|{}|{}|{}", game, country,userId);
        //}
        response.setOffers(offers);
        trackingService.incrementTracking(game, country,curTime, SocketConst.ACTION_USER_REQUEST_OFFERS);
        return response;
    }
    public UserOffers getAllOffers(String id){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        User user = userRepository.findFirstById(id).orElse(null);
        UserOffers response = new UserOffers();

        ArrayList<Offer> offers = new ArrayList<>();
        //if(user != null){
        //    response.setUserId(user.getUserId());
        //    UserRunOffer userRunOffer = userRunOfferRepository.findOneByUser_Id(user.getId()).orElse(null);
        //    if(userRunOffer != null){
        //        Set<RunOffer> runOffers = userRunOffer.getRunOffers();
        //        for (RunOffer runOffer : runOffers) {
        //            com.zingplay.models.Offer offer = runOffer.getOffer();
        //            if(offer == null){continue;}
        //            Offer offerResponse = new Offer();
        //            offerResponse.setId(runOffer.getId());
        //            offerResponse.setPriority(runOffer.getPriority());
        //            offerResponse.setIdRunOffer(runOffer.getIdRunOffer());
        //            offerResponse.setName(offer.getDisplayName());
        //            offerResponse.setPrice(offer.getPrice());
        //            offerResponse.setItems(offer.getItems());
        //            offerResponse.setBasePrice(offer.getBasePrice());
        //            offerResponse.setBonus(offer.getBonus());
        //            offerResponse.setIconNum(offer.getIconNum());
        //            offerResponse.setThemeNum(offer.getThemeNum());
        //            offerResponse.setTimeStart(runOffer.getTimeStart());
        //            offerResponse.setTimeEnd(runOffer.getTimeEnd());
        //            offers.add(offerResponse);
        //        }
        //    }else{
        //        LogUserAction.getInstance().error("getAllOffers userRunOffer null|{}|{}|{}" , game, country, id);
        //    }
        //}else{
        //    LogUserAction.getInstance().error("getAllOffers user null|{}|{}|{}" , game, country, id);
        //}
        if(user != null){
            Stream<UserObject> userObjectStream = userObjectRepository.streamAllByUser(user);
            userObjectStream.forEach(userObject -> {
                Object object = userObject.getObject();
                if (object != null) {
                    Stream<RunOffer> runOfferStream = runOfferRepository.streamAllByObject(object);
                    runOfferStream.forEach(runOffer -> {
                        Offer offerResponse = new Offer();
                        {
                            com.zingplay.models.Offer offer = runOffer.getOffer();
                            if (offer != null) {
                                offerResponse.setId(runOffer.getId());
                                offerResponse.setPriority(runOffer.getPriority());
                                offerResponse.setIdRunOffer(runOffer.getIdRunOffer());
                                offerResponse.setCountry(offer.getRegion());
                                offerResponse.setName(offer.getDisplayName());
                                offerResponse.setPrice(offer.getPrice());
                                offerResponse.setItems(offer.getItems());
                                offerResponse.setBasePrice(offer.getBasePrice());
                                offerResponse.setBonus(offer.getBonus());
                                offerResponse.setIconNum(offer.getIconNum());
                                offerResponse.setThemeNum(offer.getThemeNum());
                                offerResponse.setTimeStart(runOffer.getTimeStart());
                                offerResponse.setTimeEnd(runOffer.getTimeEnd());
                                offers.add(offerResponse);
                            }
                        }
                    });
                }
            });
        }
        response.setOffers(offers);
        return response;
    }

    public List<DataCustom> getDataGift(String userId, String game, String country, long curTime){
        List<DataCustom> list = new ArrayList<>();
        long t1 = System.currentTimeMillis();
        User user = userRepository.findFirstByUserId(Integer.parseInt(userId)).orElse(null);
        long t2 = System.currentTimeMillis();
        if(user != null){
            Date curDate = new Date(curTime);
            Stream<UserObject> userObjectStream = userObjectRepository.streamAllByUser(user);
            long t3 = System.currentTimeMillis();
            userObjectStream.forEach(userObject -> {
                Object object = userObject.getObject();
                if (object != null) {
                    List<CustomGift> giftStream = customGiftRepository.findByObjectIdAndTimeStartIsBeforeAndTimeEndIsAfter(object.getId(), curDate, curDate);
                    giftStream.forEach(gift -> {
                        DataCustom data = new DataCustom();
                        data.setDataId(gift.getId());
                        data.setDataName(gift.getGiftName());
                        data.setTypeCustom(TypeCustom.GIFT.name());
                        data.setTimeStart(gift.getTimeStart().getTime());
                        data.setTimeEnd(gift.getTimeEnd().getTime());
                        data.setItems(new HashSet<>(gift.getListItem()));
                        list.add(data);
                    });
                }
            });
            long t4 = System.currentTimeMillis();
            LogUserAction.getInstance().info("getDataGift |" + list.size() +  game +"|" +  country +"|" +  userId  +"|" + (t4-t3) +"|"+ (t3-t2) +"|"+ (t2-t1) +"|"+ (t4-t1) +"|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
        }
        return list;
    }

    public void deleteUserOffers(String id,String idOffer){
        //String game = helpers.getGame();
        //String country = helpers.getCountry();
        //String username = helpers.getUsername();
        //LogGameDesignAction.getInstance().info("deleteUserOffers|{}|{}|{}|{}|{}" , username, game, country, id, idOffer);
        //User user = userRepository.findFirstByIdAndGameAndCountry(id,game,country).orElse(null);
        //if(user != null){
        //    UserRunOffer userRunOffer = userRunOfferRepository.findOneByUser_Id(user.getId()).orElse(null);
        //    if(userRunOffer != null){
        //        Set<RunOffer> runOffers = userRunOffer.getRunOffers();
        //        runOffers.removeIf(next -> {
        //            String id2 = next.getId();
        //            return idOffer.equals(id2);
        //            //com.zingplay.models.Offer offer = next.getOffer();
        //            //if (offer != null) {
        //            //    String id1 = offer.getId();
        //            //    return id1.equals(idOffer);
        //            //}
        //            //return true;
        //        });
        //        userRunOffer.setRunOffers(runOffers);
        //        userRunOfferRepository.save(userRunOffer);
        //    }else{
        //        LogGameDesignAction.getInstance().error("deleteUserOffers userRunOffer null|{}|{}|{}|{}|{}" , username, game, country, id, idOffer);
        //    }
        //}else{
        //    LogGameDesignAction.getInstance().error("deleteUserOffers user null|{}|{}|{}|{}|{}" , username, game, country, id, idOffer);
        //}
    }

    public Page<?> getAllUser(String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(search!=null && !search.isEmpty()){
            Integer userId = Integer.parseInt(search);
            return userRepository.findByUserId(userId, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public int createAll(List<UserTracking> users) {
        List<String> userIds = getUserIds(users);
        List<User> byUserIdIn = userRepository.findByUserIdIn(userIds);
        List<UserTracking> userUpdate = users.stream().filter(userTracking -> {
            String userId = userTracking.getUserId();
            return containsUserId(byUserIdIn, userId);
        }).collect(Collectors.toList());
        List<UserTracking> userCreate = users.stream().filter(userTracking -> {
            String userId = userTracking.getUserId();
            return !containsUserId(byUserIdIn, userId);
        }).collect(Collectors.toList());
        int i = _createAll(userCreate);
        int i1 = _updateAll(byUserIdIn, userUpdate);
        return i + i1;
    }

    public User create(UserTracking userTracking) {
        if(userTracking.isCustomData()){
            return customCreate(userTracking);
        }
        LogUserAction.getInstance().info("createUserTracking|{}|{}" , helpers.getUsername(), userTracking.toString());
        Integer userId = Integer.parseInt(userTracking.getUserId());
        User user = userRepository.findFirstByUserId(userId).orElse(new User());
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(userId, userTracking.getGame(), userTracking.getCountry()).orElse(new User());
        user.setUserId(userTracking.getUserId());
        user.setTimeOnline(userTracking.getTimeOnline());
        user.setChannelPayments(userTracking.getChannelPayments());
        user.setChannelGame(userTracking.getChannelGame());
        user.setTotalGame(userTracking.getTotalGame());
        user.setTotalPaid(userTracking.getTotalPaid());
        user.setLastPaidPack(userTracking.getLastPaidPack());
        user.setTimeCreate(userTracking.getTimeCreate());
        user.setGame(userTracking.getGame());
        user.setCountry(userTracking.getCountry());
        return userRepository.save(user);
    }

    public User customCreate(UserTracking userTracking){
        LogUserAction.getInstance().info("createUserTracking|{}|{}" , helpers.getUsername(), userTracking.toString());
        Integer userId = Integer.parseInt(userTracking.getUserId());
        User user = userRepository.findFirstByUserId(userId).orElse(new User());
        user.setUserId(userTracking.getUserId());
        user.setGame(userTracking.getGame());
        user.setCountry(userTracking.getCountry());
        user.setTimeCreate(userTracking.getTimeCreate());
        user.setTimeOnline(userTracking.getTimeOnline());
        user.setTrackingFloat(userTracking.getTrackingFloat());
        user.setTrackingLong(userTracking.getTrackingLong());
        user.setTrackingStr(userTracking.getTrackingStr());
        user.setTrackingObject(userTracking.getTrackingObject());
        user.setTrackingDuration(userTracking.getTrackingDuration());
        return userRepository.save(user);
    }

    public User updateUser(String id, User userTracking){
        if(userTracking.isCustomData()){
            return customUpdateUser(id, userTracking);
        }
        LogUserAction.getInstance().info("updateUserTracking|{}|{}" , helpers.getUsername(), userTracking.toString());
        return userRepository.findById(id).map(user -> {
            user.setUserId(userTracking.getUserId());
            user.setTimeCreate(userTracking.getTimeCreate());
            user.setTimeOnline(userTracking.getTimeOnline());
            user.setChannelPayments(userTracking.getChannelPayments());
            user.setChannelGame(userTracking.getChannelGame());
            user.setTotalGame(userTracking.getTotalGame());
            user.setTotalPaid(userTracking.getTotalPaid());
            user.setLastPaidPack(userTracking.getLastPaidPack());
            user.setGame(userTracking.getGame());
            user.setCountry(userTracking.getCountry());
            return userRepository.save(user);
        }).orElse(null);
    }

    public User customUpdateUser(String id, User userTracking){
        return userRepository.findById(id).map(user -> {
            user.setUserId(userTracking.getUserId());
            user.setTimeCreate(userTracking.getTimeCreate());
            user.setTimeOnline(userTracking.getTimeOnline());
            user.setTrackingFloat(userTracking.getTrackingFloat());
            user.setTrackingLong(userTracking.getTrackingLong());
            user.setTrackingStr(userTracking.getTrackingStr());
            user.setTrackingObject(userTracking.getTrackingObject());
            user.setTrackingDuration(userTracking.getTrackingDuration());
            user.setGame(userTracking.getGame());
            user.setCountry(userTracking.getCountry());
            return userRepository.save(user);
        }).orElse(null);
    }

    public void deleteUser(String id){
        LogGameDesignAction.getInstance().info("deleteUserTracking|{}|{}" , helpers.getUsername(), id);
        userRepository.deleteById(id);
    }

    private int _createAll(List<UserTracking> list){
        List<User> users = toUsers(list);
        return userRepository.saveAll(users).size();
    }
    private int _updateAll(List<User> byUserIdIn, List<UserTracking> list){
        byUserIdIn.forEach(user -> {
            String userId = user.getUserId();
            UserTracking userTracking = find(list, userId);
            user.setTimeOnline(userTracking.getTimeOnline());
            user.setChannelPayments(userTracking.getChannelPayments());
            user.setChannelGame(userTracking.getChannelGame());
            user.setTotalGame(userTracking.getTotalGame());
            user.setTotalPaid(userTracking.getTotalPaid());
            user.setLastPaidPack(userTracking.getLastPaidPack());
            user.setGame(userTracking.getGame());
            user.setCountry(userTracking.getCountry());
        });
        return userRepository.saveAll(byUserIdIn).size();
    }
    private UserTracking find(List<UserTracking> list, String userId){
        return list.stream().filter(userTracking -> userId.equals(userTracking.getUserId())).findFirst().orElse(null);
    }
    private List<User> toUsers(List<UserTracking> list){
        return list.stream().map(userTracking -> {
            User user = new User();
            user.setUserId(userTracking.getUserId());
            user.setTimeOnline(userTracking.getTimeOnline());
            user.setChannelPayments(userTracking.getChannelPayments());
            user.setChannelGame(userTracking.getChannelGame());
            user.setTotalGame(userTracking.getTotalGame());
            user.setTotalPaid(userTracking.getTotalPaid());
            user.setLastPaidPack(userTracking.getLastPaidPack());
            user.setTimeCreate(userTracking.getTimeCreate());
            user.setGame(userTracking.getGame());
            user.setCountry(userTracking.getCountry());
            return user;
        }).collect(Collectors.toList());
    }
    private boolean containsUserId(final List<User> users, final String userId){
        //return users.stream().map(User::getIdUser).filter(userId::equals).findFirst().isPresent();
        return users.stream().map(User::getUserId).anyMatch(userId::equals);
    }
    private List<String> getUserIds(List<UserTracking> users){
        List<String> results = new ArrayList<>();
        for (UserTracking user : users) {
            results.add(user.getUserId());
        }
        return results;
    }

    public void logTrackingUserReceiveOffer(String game, String country, UserOffers userOffer) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
        String userId = userOffer.getUserId();
        List<Offer> offers = userOffer.getOffers();
        if(offers != null){
            if(offers.isEmpty()){
                return;
            }
            Integer id = Integer.parseInt(userId);
            User user = userRepository.findFirstByUserId(id).orElse(null);
            // User user = userRepository.findFirstByUserIdAndGameAndCountry(id, game, country).orElse(null);
            if(user != null){
                for (Offer offer : offers) {
                    String id1 = offer.getId();
                    RunOffer runOffer = runOfferRepository.findById(id1).orElse(null);
                    if (runOffer == null) {
                        //khong ton tai run offer
                    } else {
                        UserReceived userReceived = userReceivedRepository.findFirstByUserAndRunOffer(user, runOffer).orElse(null);
                        if (userReceived != null) {
                            //da ton tai
                        } else {
                            userReceived = new UserReceived();
                            userReceived.setRunOffer(runOffer);
                            userReceived.setUser(user);
                            userReceivedRepository.save(userReceived);
                            long countReceived = runOffer.getCountReceived();
                            runOffer.setCountReceived(countReceived + 1);
                            runOfferRepository.save(runOffer);
                            LogUserAction.getInstance().info("logTrackingUserReceiveOffer|"  + game +"|" + country + "|" + userId +"|" + offers.size() +"|" + runOffer.getIdRunOffer());
                        }
                    }
                }
            }else{
                LogErrorAction.getInstance().error("logTrackingUserReceiveOffer|user not found|" + userId  +"|" + game +"|" + country + "|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
            }
        }
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }

    //todo sua them
    //region v2 -> them gia local
    @Transactional(propagation = Propagation.REQUIRED)
    public void logTrackingBuyOfferV2(UserTrackingBuyOfferV2 tracking, String game, String country){
        reportOfferService.logTrackingBuyOfferV2(tracking,game,country);
        trackingService.incrementTracking(game, country,tracking.getTimeCurrent(), SocketConst.ACTION_USER_BUY_OFFER);
    }

    public void logTrackingUserGetDataCustom(String game, String country, UserDataCustom userDataCustom){
        trackingService.incrementTracking(game, country, userDataCustom.getTimeCurrent(), SocketConst.ACTION_GET_DATA_CUSTOM);
    }

    public void logTrackingUserReceiveDataCustom(String game, String country, UserReceiveDataCustom userDataCustom){
        customDataService.updateNumReceiveDataCustom(game, country, userDataCustom);
        trackingService.incrementTracking(game, country, userDataCustom.getTimeCurrent(), SocketConst.ACTION_RECEIVE_DATA_CUSTOM);
    }

    public com.zingplay.socket.v2.response.UserOffers getOffersV2(String userId, String game, String country, long curTime, String countryPrice){
        if(countryPrice == null || countryPrice.isEmpty()){
            countryPrice = country;//mặc định <- lấy giá country game
        }
        long t1 = System.currentTimeMillis();
        // User user = userRepository.findFirstByUserIdAndGameAndCountry(Integer.parseInt(userId),game,country).orElse(null);
        User user = userRepository.findFirstByUserId(Integer.parseInt(userId)).orElse(null);
        long t2 = System.currentTimeMillis();
        com.zingplay.socket.v2.response.UserOffers response = new com.zingplay.socket.v2.response.UserOffers();
        response.setUserId(userId);

        ArrayList<com.zingplay.socket.v2.response.Offer> offers = new ArrayList<>();
        //User user = userRepository.findById(userId).orElse(null);
        //test get truc tiep
        if(user != null){
            Date curDate = new Date(curTime);
            Stream<UserObject> userObjectStream = userObjectRepository.streamAllByUser(user);
            long t3 = System.currentTimeMillis();
            String finalCountryPrice = countryPrice;
            userObjectStream.forEach(userObject -> {
                Object object = userObject.getObject();
                if (object != null) {
                    Stream<RunOffer> runOfferStream = runOfferRepository.streamAllByObjectAndTimeEndAfter(object,curDate);
                    runOfferStream.forEach(runOffer -> {
                        com.zingplay.socket.v2.response.Offer offerResponse = new com.zingplay.socket.v2.response.Offer();
                        //Date timeEnd = runOffer.getTimeEnd();
                        //if(curDate.before(timeEnd))
                        {
                            com.zingplay.models.Offer offer = runOffer.getOffer();
                            if (offer != null) {
                                offerResponse.setId(runOffer.getId());
                                offerResponse.setName(offer.getDisplayName());
                                offerResponse.setPriority(runOffer.getPriority());
                                offerResponse.setIconNum(offer.getIconNum());
                                offerResponse.setThemeNum(offer.getThemeNum());
                                offerResponse.setTimeStart(runOffer.getTimeStart().getTime());
                                offerResponse.setTimeEnd(runOffer.getTimeEnd().getTime());
                                offerResponse.setItems(offer.getItems());

                                Price orDefault = _getPriceOfferViaCountry(offer,finalCountryPrice);
                                if(orDefault != null){
                                    offerResponse.setPrice(orDefault);
                                    offers.add(offerResponse);
                                }
                            }
                        }
                    });
                }
            });
            long t4 = System.currentTimeMillis();
            LogUserAction.getInstance().info("getOffers test|" + offers.size() +  game +"|" +  country +"|" +  userId  +"|" + (t4-t3) +"|"+ (t3-t2) +"|"+ (t2-t1) +"|"+ (t4-t1) +"|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
        }
        response.setOffers(offers);
        trackingService.incrementTracking(game, country,curTime, SocketConst.ACTION_USER_REQUEST_OFFERS);
        return response;
    }

    private Price _getPriceOfferViaCountry(com.zingplay.models.Offer offer, String finalCountryPrice) {

        /* Một số ví dụ:
         * Nếu set giá default: ph(50php) + custom: all(100php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         * Nếu set giá default: all(100php) + custom: ph(50php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         * Nếu set giá default: ph(20php) + custom: ph(50php), all(100php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         * Nếu set giá default: all(20php) + custom: ph(50php), all(100php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         */

        //1. tìm country theo giá custom nếu có
        //2. tìm country theo giá default
        //3. tìm country all theo giá custom
        //4. tìm country all theo giá default

        Price orDefault = null;
        Set<com.zingplay.models.Price> prices = offer.getPrices();
        //1.
        if(prices != null && !prices.isEmpty()){
            for (com.zingplay.models.Price price : prices) {
                if(finalCountryPrice.equals(price.getCountry())){
                    return Price.from(price);
                }
            }
        }
        //2.
        String region = offer.getRegion();
        if(finalCountryPrice.equals(region)){
            orDefault = new Price();
            orDefault.setCountry(region);
            orDefault.setPrice(offer.getPrice());
            orDefault.setBasePrice(offer.getBasePrice());
            orDefault.setBonus(offer.getBonus());
            orDefault.setCurrency(offer.getCurrency());
            Set<String> channels = offer.getChannels();
            orDefault.setChannels(channels == null? new HashSet<>(): channels);
            return orDefault;
        }

        String countryAll = "all";
        //3.
        if(prices != null && !prices.isEmpty()) {
            for (com.zingplay.models.Price price : prices) {
                if (countryAll.equals(price.getCountry())) {
                    return Price.from(price);
                }
            }
        }
        //4.
        if(countryAll.equals(region)){
            orDefault = new Price();
            orDefault.setCountry(region);
            orDefault.setPrice(offer.getPrice());
            orDefault.setBasePrice(offer.getBasePrice());
            orDefault.setBonus(offer.getBonus());
            orDefault.setCurrency(offer.getCurrency());
            Set<String> channels = offer.getChannels();
            orDefault.setChannels(channels == null? new HashSet<>(): channels);
            return orDefault;
        }
        return orDefault;
    }

    public com.zingplay.socket.v2.response.Offer getOffersDetail(String idOffer, String game, String country, String countryPrice){
        if(countryPrice == null || countryPrice.isEmpty()){
            countryPrice = country;//mặc định <- lấy giá country game
        }
        com.zingplay.socket.v2.response.Offer offerResponse = new com.zingplay.socket.v2.response.Offer();
        offerResponse.setId(idOffer);
        RunOffer runOffer = runOfferRepository.findById(idOffer).orElse(null);
        if(runOffer != null){
            com.zingplay.models.Offer offer = runOffer.getOffer();
            if (offer != null) {
                offerResponse.setId(runOffer.getId());
                offerResponse.setName(offer.getDisplayName());
                offerResponse.setPriority(runOffer.getPriority());
                offerResponse.setIconNum(offer.getIconNum());
                offerResponse.setThemeNum(offer.getThemeNum());
                offerResponse.setTimeStart(runOffer.getTimeStart().getTime());
                offerResponse.setTimeEnd(runOffer.getTimeEnd().getTime());
                offerResponse.setItems(offer.getItems());
                Price orDefault = _getPriceOfferViaCountry(offer,countryPrice);
                if(orDefault != null){
                    offerResponse.setPrice(orDefault);
                }
            }
            LogUserAction.getInstance().info("getOffersDetail test|" + idOffer +  game +"|" +  country +"|" +  idOffer);
        }
        return offerResponse;
    }
    public void logTrackingUserReceiveOfferV2(String game, String country, com.zingplay.socket.v2.response.UserOffers userOffer) {
        reportOfferService.logTrackingUserReceiveOfferV2(game,country,userOffer);
    }

}
