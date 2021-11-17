package com.zingplay.module.report.v2;

import com.zingplay.models.RunOffer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReportOfferDailyRepository extends MongoRepository<ReportOfferDaily, String> {
  Optional<ReportOfferDaily> findByDateAndGameAndCountry(String date, String game, String country);
  Optional<ReportOfferDaily> findByDateAndGameAndCountryAndRunOffer(String date, String game, String country, RunOffer runOffer);
  List<ReportOfferDaily> findAllByGameAndCountryAndTimeCreateBetween(String game, String country, Date timeCreate, Date timeCreate1);
  void deleteAllByGameAndCountry(String game, String country);

}
