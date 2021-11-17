package com.zingplay.repository;

import com.zingplay.models.RunOffer;
import com.zingplay.models.User;
import com.zingplay.models.UserReceived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserReceivedRepository extends MongoRepository<UserReceived, String> {
    Page<UserReceived> findAllByUser(User user, Pageable pageable);
    Page<UserReceived> findAllByUserAndRunOfferIn(User user, List<RunOffer> RunOffer, Pageable pageable);

    public void deleteAllByRunOffer(RunOffer RunOffer);
    public void deleteAllByUser(User user);
    Stream<UserReceived> streamAllByRunOffer(RunOffer RunOffer);
    Stream<UserReceived> streamAllByUser(User user);
    Page<UserReceived> findByRunOffer_Id(String idRunOffer, Pageable pageable);
    Optional<UserReceived> findFirstByUserAndRunOffer(User user, RunOffer RunOffer);
    Optional<UserReceived> findFirstByUserAndRunOffer_Id(User user, String id);
    Page<UserReceived> findByUserIsInAndRunOffer_Id(List<User> users, String RunOffer_id, Pageable pageable);

}
