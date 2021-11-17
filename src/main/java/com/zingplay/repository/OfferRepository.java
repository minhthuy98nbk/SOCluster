package com.zingplay.repository;

import com.zingplay.models.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends MongoRepository<Offer, String> {
  Page<Offer> findByIdOfferContainingAndGameAndCountry(String search, String game, String country, Pageable pageable);
  Page<Offer> findByIdAndGameAndCountry(String search, String game, String country, Pageable pageable);
  Page<Offer> findAllByGameAndCountry(String game, String country, Pageable pageable);
  List<Offer> findByIdOfferIn(List<String> idOffers);
    Optional<Offer> findFirstByIdOffer(String idOffer);
    Optional<Offer> findFirstByIdOfferAndGameAndCountry(String idOffer, String game, String country);
    boolean existsObjectByIdOfferAndGameAndCountry(String idObject, String game, String country);
  void deleteAllByGameAndCountry(String game, String country);

}
