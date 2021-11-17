package com.zingplay.module.tracking;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrackingRepository extends MongoRepository<Tracking, String> {
  Optional<Tracking> findByDateAndGameAndCountry(String date, String game, String country);
  List<Tracking> findAllByGameAndCountryAndTimeCreateBetween(String game, String country, Date timeCreate, Date timeCreate1);
  void deleteAllByGameAndCountry(String game, String country);

}
