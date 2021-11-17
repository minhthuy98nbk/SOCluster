package com.zingplay.service.user;

import com.zingplay.beans.Offer;
import com.zingplay.helpers.Helpers;
import com.zingplay.log.LogGameDesignAction;
import com.zingplay.models.Price;
import com.zingplay.payload.request.OfferCreate;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final Helpers helpers;
    private final LogService logService;

    @Autowired
    public OfferService(OfferRepository offerRepository, Helpers helpers, LogService logService) {
        this.offerRepository = offerRepository;
        this.helpers = helpers;
        this.logService = logService;
    }

    public Page<?> getAllOffer(String search, Pageable pageable){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        if(search!=null && !search.isEmpty()){
            Page<com.zingplay.models.Offer> byIdOfferContainingAndGameAndCountry = offerRepository.findByIdOfferContainingAndGameAndCountry(search, game, country, pageable);
            if(byIdOfferContainingAndGameAndCountry.getContent().isEmpty()){
                byIdOfferContainingAndGameAndCountry = offerRepository.findByIdAndGameAndCountry(search, game, country, pageable);
            }
            return byIdOfferContainingAndGameAndCountry;
        }
        return offerRepository.findAllByGameAndCountry(game, country, pageable);
    }

    public int createAll(List<Offer> offers) {
        for (Offer offer : offers) {
            offer.autoTrim();
        }
        //tao list Offer -> override = 1 -> update;
        List<String> idOffers = getOfferIds(offers);
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        LogGameDesignAction.getInstance().info("createAllOffers|{}|{}|{}|{}" , username, game, country, offers.size());
        offers.forEach(offer -> {
            LogGameDesignAction.getInstance().info("createAllOffers|{}|{}|{}|{}" , username, game, country, offer.toString());
            offer.setGame(game);
            offer.setCountry(country);
        });
        List<com.zingplay.models.Offer> offerExists = offerRepository.findByIdOfferIn(idOffers);

        List<Offer> offerUpdate = offers.stream().filter(offer -> {
            String idOffer = offer.getIdOffer();
            return containsIdOffers(offerExists, idOffer);
        }).collect(Collectors.toList());

        List<Offer> offerCreates = offers.stream().filter(offer -> {
            String idOffer = offer.getIdOffer();
            return !containsIdOffers(offerExists, idOffer);
        }).collect(Collectors.toList());
        int i = _createAll(offerCreates);
        int i1 = _updateAll(offerExists, offerUpdate);
        logService.addLog("import offer from file [" + (i + i1) +"] offer");
        return i + i1;
    }

    public ResponseEntity<?> create(Offer OfferCreate) {
        OfferCreate.autoTrim();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        OfferCreate.setGame(game);
        OfferCreate.setCountry(country);
        Set<Price> prices = OfferCreate.getPrices();
        if(prices == null || prices.isEmpty()){
            OfferCreate.setPrices(null);
        }
        LogGameDesignAction.getInstance().info("create offer|{}|{}|{}|{}" , username, game, country, OfferCreate.toString());
        boolean b = offerRepository.existsObjectByIdOfferAndGameAndCountry(OfferCreate.getIdOffer(), game, country);
        if(b){
            LogGameDesignAction.getInstance().error("create offer exists|{}|{}|{}|{}" , username, game, country, OfferCreate.getIdOffer());
            return ResponseEntity.badRequest().body(new MessageResponse("offer Id had exists!!"));
        }else{
            LogGameDesignAction.getInstance().error("create offer created|{}|{}|{}|{}" , username, game, country, OfferCreate.getIdOffer());
            com.zingplay.models.Offer Offer = toOfferModel(OfferCreate);
            logService.addLog("create offer [" + Offer.getIdOffer() +"]");
            return ResponseEntity.ok().body(offerRepository.save(Offer));
        }

    }

    public com.zingplay.models.Offer updateOffer(String id, Offer offer){
        offer.autoTrim();
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        offer.setGame(game);
        offer.setCountry(country);
        LogGameDesignAction.getInstance().error("create offer updating|{}|{}|{}|{}" , username, game, country, offer.getIdOffer());
        return offerRepository.findById(id).map(objUpdate -> {
            objUpdate.setIdOffer(offer.getIdOffer());
            objUpdate.setGame(offer.getGame());
            objUpdate.setCountry(offer.getCountry());
            objUpdate.setRegion(offer.getRegion());

            objUpdate.setNameOffer(offer.getNameOffer());
            objUpdate.setDisplayName(offer.getDisplayName());
            objUpdate.setNote(offer.getNote());

            objUpdate.setCurrency(offer.getCurrency());
            objUpdate.setPrice(offer.getPrice());
            objUpdate.setBasePrice(offer.getBasePrice());
            objUpdate.setBonus(offer.getBonus());
            objUpdate.setIconNum(offer.getIconNum());
            objUpdate.setThemeNum(offer.getThemeNum());
            objUpdate.setItems(offer.getItems());
            objUpdate.setChannels(offer.getChannels());
            Set<Price> prices = offer.getPrices();
            if(prices == null || prices.isEmpty()){
                objUpdate.setPrices(null);
            }else{
                objUpdate.setPrices(prices);
            }

            LogGameDesignAction.getInstance().error("create offer updated|{}|{}|{}|{}" , username, game, country, objUpdate.getIdOffer());
            logService.addLog("update offer [" + offer.getIdOffer() +"]");
            return offerRepository.save(objUpdate);
        }).orElse(null);
    }

    public void deleteOffer(String id){
        String game = helpers.getGame();
        String country = helpers.getCountry();
        String username = helpers.getUsername();
        com.zingplay.models.Offer offer = offerRepository.findById(id).orElse(null);
        if(offer != null){
            logService.addLog("delete offer [" + offer.getIdOffer() +"]");
            LogGameDesignAction.getInstance().info("deleteOffer|{}|{}|{}|{}|{}" , username, game, country, offer.getIdOffer(), id);
        }else{
            LogGameDesignAction.getInstance().info("deleteOffer|{}|{}|{}|{}" , username, game, country, id);
        }
        offerRepository.deleteById(id);
    }

    private int _createAll(List<Offer> list){
        List<com.zingplay.models.Offer> Offers = toOffers(list);
        return offerRepository.saveAll(Offers).size();
    }
    private int _updateAll(List<com.zingplay.models.Offer> byOfferIn, List<Offer> list){
        byOfferIn.forEach(objUpdate -> {
            String idOffer = objUpdate.getIdOffer();
            Offer offer = find(list, idOffer);
            if(!offer.isOverride()) return;

            objUpdate.setIdOffer(offer.getIdOffer());
            objUpdate.setTimeUpdate(new Date());
            objUpdate.setGame(offer.getGame());
            objUpdate.setRegion(offer.getRegion());
            objUpdate.setCountry(offer.getCountry());

            objUpdate.setNameOffer(offer.getNameOffer());
            objUpdate.setDisplayName(offer.getDisplayName());
            objUpdate.setNote(offer.getNote());

            objUpdate.setPrice(offer.getPrice());
            objUpdate.setBasePrice(offer.getBasePrice());
            objUpdate.setBonus(offer.getBonus());
            objUpdate.setIconNum(offer.getIconNum());
            objUpdate.setThemeNum(offer.getThemeNum());
            objUpdate.setItems(offer.getItems());


            //v2 custom price
            String currency = offer.getCurrency();
            Set<String> channels = offer.getChannels();
            Set<Price> prices = offer.getPrices();
            objUpdate.setCurrency(currency);
            objUpdate.setChannels(channels);
            objUpdate.setPrices(prices);
            //end v2 custom price
        });
        return offerRepository.saveAll(byOfferIn).size();
    }
    private Offer find(List<Offer> list, String idOffer){
        return list.stream().filter(Offer -> idOffer.equals(Offer.getIdOffer())).findFirst().orElse(null);
    }
    private List<com.zingplay.models.Offer> toOffers(List<Offer> list){
        return list.stream().map(this::toOfferModel).collect(Collectors.toList());
    }
    private com.zingplay.models.Offer toOfferModel(Offer offer){
        com.zingplay.models.Offer objUpdate = new com.zingplay.models.Offer();
        objUpdate.setIdOffer(offer.getIdOffer());

        objUpdate.setGame(offer.getGame());
        objUpdate.setCountry(offer.getCountry());
        objUpdate.setRegion(offer.getRegion());

        objUpdate.setNameOffer(offer.getNameOffer());
        objUpdate.setDisplayName(offer.getDisplayName());
        objUpdate.setNote(offer.getNote());

        objUpdate.setPrice(offer.getPrice());
        objUpdate.setBasePrice(offer.getBasePrice());
        objUpdate.setBonus(offer.getBonus());
        objUpdate.setIconNum(offer.getIconNum());
        objUpdate.setThemeNum(offer.getThemeNum());
        objUpdate.setItems(offer.getItems());

        //v2 custom price
        String currency = offer.getCurrency();
        Set<String> channels = offer.getChannels();
        Set<Price> prices = offer.getPrices();
        objUpdate.setCurrency(currency);
        objUpdate.setChannels(channels);
        objUpdate.setPrices(prices);
        //end v2 custom price
        return objUpdate;
    }
    private boolean containsIdOffers(final List<com.zingplay.models.Offer> Offers, final String idOffer){
        //return users.stream().map(User::getIdUser).filter(userId::equals).findFirst().isPresent();
        return Offers.stream().map(com.zingplay.models.Offer::getIdOffer).anyMatch(idOffer::equals);
    }
    private List<String> getOfferIds(List<Offer> Offers){
        List<String> results = new ArrayList<>();
        for (Offer user : Offers) {
            results.add(user.getIdOffer());
        }
        return results;
    }
}
