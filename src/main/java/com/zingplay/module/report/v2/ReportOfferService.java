package com.zingplay.module.report.v2;

import com.zingplay.helpers.Helpers;
import com.zingplay.log.*;
import com.zingplay.models.*;
import com.zingplay.module.report.v1.ReportBuyOfferRepository;
import com.zingplay.module.telegram.TelegramConst;
import com.zingplay.module.telegram.TelegramController;
import com.zingplay.repository.*;
import com.zingplay.socket.v2.request.UserTrackingBuyOfferV2;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportOfferService {
    private final UserRepository userRepository;
    private final UserObjectRepository userObjectRepository;
    private final UserBuyOfferRepository userBuyOfferRepository;
    private final RunOfferRepository runOfferRepository;
    private final CustomGiftRepository customGiftRepository;
    private final UserReceivedRepository userReceivedRepository;
    private final ReportBuyOfferRepository reportBuyOfferRepository;
    private final ReportOfferDailyRepository reportOfferDailyRepository;
    private Helpers helpers;

    @Autowired
    public ReportOfferService(UserRepository userRepository, UserObjectRepository userObjectRepository, @Lazy UserBuyOfferRepository userBuyOfferRepository, RunOfferRepository runOfferRepository, CustomGiftRepository customGiftRepository, UserReceivedRepository userReceivedRepository, ReportBuyOfferRepository reportBuyOfferRepository, ReportOfferDailyRepository reportOfferDailyRepository, Helpers helpers) {
        this.userRepository = userRepository;
        this.userObjectRepository = userObjectRepository;
        this.userBuyOfferRepository = userBuyOfferRepository;
        this.runOfferRepository = runOfferRepository;
        this.customGiftRepository = customGiftRepository;
        this.userReceivedRepository = userReceivedRepository;
        this.reportBuyOfferRepository = reportBuyOfferRepository;
        this.reportOfferDailyRepository = reportOfferDailyRepository;
        this.helpers = helpers;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserBuyOffer userBuyOffer(User user, String id_offer, long timeCurrent, String countryBuy, float price){
        //neu "" => chuyen ve country của game;
        String country = user.getCountry();
        if(country == null || countryBuy.isEmpty()) { countryBuy = country; }
        countryBuy = countryBuy.toUpperCase();
        //todo log report theo country
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
            com.zingplay.models.Offer offer = runOffer.getOffer();
            LogUserAction.getInstance().info("userBuyOffer|{}|{}|{}|{}", user.getUserId(), offer.getIdOffer(), offer.getPrice(), timeCurrent);
            float priceOffer = Helpers.getPriceFrom(offer, countryBuy);
            if(priceOffer != price){
                if(price != 0){
                    String format = String.format("[%s %s]userBuyOffer Sai giá", game, country);
                    LogUserAction.getInstance().error("userBuyOffer|{}|{}|{}|{}", user.getUserId(), offer.getIdOffer(), offer.getPrice(), format);
                    TelegramController.getInstance().sendWarning(TelegramConst.BUY_OFFER_WRONG, game, country, format);
                }else{
                    price = priceOffer;
                }
            }
            //tăng trong runOffer
            //thêm record userbuyoffer
            //tăng log reportdailu
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

            ReportOfferDaily reportOfferDaily = reportOfferDailyRepository.findByDateAndGameAndCountryAndRunOffer(strDate, game, country, runOffer).orElseGet(() -> {
                ReportOfferDaily r = new ReportOfferDaily();
                r.setGame(game);
                r.setCountry(country);
                r.setDate(strDate);
                r.setTimeCreate(timeCreate);
                r.setRunOffer(runOffer);
                r.setName(offer.getNameOffer());
                return r;
            });
            reportOfferDaily.addUserBuyOffer(countryBuy,price);

            runOfferRepository.save(runOffer);
            reportBuyOfferRepository.save(reportBuyOffer);
            reportOfferDailyRepository.save(reportOfferDaily);
            return userBuyOfferRepository.save(userBuyOffer);
        }
        return null;
    }

    //todo sua them
    //region v2 -> them gia local
    @Transactional(propagation = Propagation.REQUIRED)
    public void logTrackingBuyOfferV2(UserTrackingBuyOfferV2 tracking, String game, String country){
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

        float price = tracking.getPrice();
        String idOffer = tracking.getIdOffer();
        long timeCurrent = tracking.getTimeCurrent();
        userBuyOffer(user, idOffer, timeCurrent, country,price);
    }

    public void logTrackingUserReceiveOfferV2(String game, String country, com.zingplay.socket.v2.response.UserOffers userOffer) {
        MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
        String userId = userOffer.getUserId();
        List<com.zingplay.socket.v2.response.Offer> offers = userOffer.getOffers();
        if(offers != null){
            if(offers.isEmpty()){
                return;
            }
            Integer id = Integer.parseInt(userId);
            User user = userRepository.findFirstByUserId(id).orElse(null);
            if(user != null){
                for (com.zingplay.socket.v2.response.Offer offer : offers) {
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
                            LogUserAction.getInstance().info("logTrackingUserReceiveOffer|" + game + "|" + country + "|" + userId +"|" + offers.size() +"|" + runOffer.getIdRunOffer());
                        }
                    }
                }
            }else{
                LogErrorAction.getInstance().error("logTrackingUserReceiveOffer|user not found|" + userId  +"|" + game +"|" + country + "|" + MultiTenantMongoDbFactory.getDatabaseNameForCurrentThread());
            }
        }
        MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    }

}
