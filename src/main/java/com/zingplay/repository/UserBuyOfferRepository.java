package com.zingplay.repository;

import com.zingplay.models.RunOffer;
import com.zingplay.models.User;
import com.zingplay.models.UserBuyOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Stream;

public interface UserBuyOfferRepository extends MongoRepository<UserBuyOffer, String> {
    Page<UserBuyOffer> findByRunOffer_IdAndUserIn(String id, List<User> users, Pageable pageable);
    Page<UserBuyOffer> findByRunOffer_Id(String id, Pageable pageable);
    Page<UserBuyOffer> findAll(Pageable pageable);

    Stream<UserBuyOffer> streamAllByRunOffer(RunOffer runOffer);

    void deleteAllByUser(User user);
}
