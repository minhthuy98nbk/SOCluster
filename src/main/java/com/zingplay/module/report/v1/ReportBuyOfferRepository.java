package com.zingplay.module.report.v1;

import com.zingplay.models.Object;
import com.zingplay.models.ReportBuyOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReportBuyOfferRepository extends MongoRepository<ReportBuyOffer, String> {
  Optional<ReportBuyOffer> findByDateAndGameAndCountry(String date, String game,String country);
  List<ReportBuyOffer> findAllByGameAndCountryAndTimeCreateBetween(String game,String country,Date timeCreate,Date timeCreate1);
  void deleteAllByGameAndCountry(String game, String country);

}
