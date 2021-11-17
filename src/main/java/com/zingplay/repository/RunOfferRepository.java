package com.zingplay.repository;

import com.zingplay.models.Object;
import com.zingplay.models.Offer;
import com.zingplay.models.RunOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface RunOfferRepository extends MongoRepository<RunOffer, String> {
    Page<RunOffer> findByNoteContainingAndGameAndCountry(String search,String game, String country, Pageable pageable);
    Page<RunOffer> findByIdRunOfferContainingAndGameAndCountry(String search,String game, String country, Pageable pageable);
    Page<RunOffer> findByIdAndGameAndCountry(String search,String game, String country, Pageable pageable);
    Page<RunOffer> findByObjectIn(List<Object> objects, Pageable pageable);
    Page<RunOffer> findByOfferIn(List<Offer> offers, Pageable pageable);
    Page<RunOffer> findAllByGameAndCountry(String game, String country, Pageable pageable);
    Page<RunOffer> findAllByGameAndCountryAndTimeCreateBeforeAndTimeEndAfter(String game, String country, Date to, Date from, Pageable pageable);
    Page<RunOffer> findAllByGameAndCountryAndTimeStartBeforeAndTimeEndAfter(String game, String country, Date to, Date from, Pageable pageable);
    Optional<RunOffer> findFirstByObject_IdObjectAndOffer_IdOffer(String idObject, String idOffer);
    Optional<RunOffer> findFirstByIdRunOfferAndGameAndCountry(String idRunOffer,String game,String country);
    Optional<RunOffer> findFirstByStatus(int status);
    Stream<RunOffer> streamAllByObject(Object object);
    Stream<RunOffer> streamAllByObjectAndTimeEndAfter(Object object, Date time);
    List<RunOffer> findAllByObject(Object object);
    List<RunOffer> findAllByStatusIn(List<Integer> status);

    // (end > from && start < to)
    List<RunOffer> findAllByGameAndCountryAndTimeStartBeforeAndTimeEndAfter(String game, String country, Date to, Date from);

    boolean existsByIdRunOfferAndGameAndCountry(String idRunOffer,String game,String country);
    boolean existsByStatusIn(List<Integer> status);
    void deleteAllByGameAndCountry(String game, String country);


}
